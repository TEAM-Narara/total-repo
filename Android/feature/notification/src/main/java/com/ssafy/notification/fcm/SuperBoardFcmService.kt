package com.ssafy.notification.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.text.SpannableString
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ssafy.designsystem.R
import com.ssafy.fcm.UpdateFcmTokenUseCase
import com.ssafy.member.GetMemberUseCase
import com.ssafy.model.user.User
import com.ssafy.notification.fcm.data.BoardMessage
import com.ssafy.notification.fcm.data.CardMessage
import com.ssafy.notification.fcm.data.FcmMessage
import com.ssafy.notification.fcm.data.HomeMessage
import com.ssafy.notification.fcm.data.WorkspaceMessage
import com.ssafy.notification.util.toSpanString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class SuperBoardFcmService : FirebaseMessagingService() {

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    @Inject
    lateinit var getMemberUseCase: GetMemberUseCase

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { updateFcmTokenUseCase(token) }
                .onFailure { it.printStackTrace() }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        runCatching { handleMessage(remoteMessage) }
            .onFailure { it.printStackTrace() }
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        Log.d("FCM", "message: $remoteMessage")
        remoteMessage.notification
        remoteMessage.data
        val message = remoteMessage.notification?.title.toString().toSpanString()
        val data = remoteMessage.data

        val goTo = runCatching {
            val direction = remoteMessage.data["goTo"] ?: ""
            FcmDestination.valueOf(direction)
        }.fold(
            onSuccess = { it },
            onFailure = { FcmDestination.HOME }
        )

        val uniId = Random.nextInt(0, Int.MAX_VALUE)
        val fcmDirection = convertToMessage(goTo, data)
        val pendingIntent = setPendingIntent(fcmDirection, uniId)
        sendNotification(pendingIntent, message, fcmDirection.manOfActionId, uniId)
    }

    private fun convertToMessage(goTo: FcmDestination, data: Map<String, String>): FcmMessage {
        println(data)
        val gson = Gson()
        val jsonString = gson.toJson(data)

        return when (goTo) {
            FcmDestination.HOME -> gson.fromJson(jsonString, HomeMessage::class.java)
            FcmDestination.WORKSPACE -> gson.fromJson(jsonString, WorkspaceMessage::class.java)
            FcmDestination.BOARD -> gson.fromJson(jsonString, BoardMessage::class.java)
            FcmDestination.CARD -> gson.fromJson(jsonString, CardMessage::class.java)
        }
    }

    private fun setPendingIntent(fcmMessage: FcmMessage, uniId: Int): PendingIntent {
        val channelId = CHANNEL_ID
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelName = CHANNEL_NAME
        val channelDescription = CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, Class.forName(MAIN_CLASS)).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(FCM_KEY, fcmMessage)
        }

        return PendingIntent.getActivity(
            this, uniId, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun sendNotification(
        intent: PendingIntent,
        message: SpannableString,
        manOfActionId: Long?,
        uniId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val member: User? = manOfActionId?.let { getMemberUseCase(it) }?.firstOrNull()
            val url = member?.profileImgUrl ?: ""
            val bitmap = BitmapFactory.decodeFile(url) ?: BitmapFactory.decodeResource(
                resources,
                R.drawable.alarm
            )

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder =
                NotificationCompat.Builder(this@SuperBoardFcmService, CHANNEL_ID)
                    .setContentTitle(TITLE)
                    .setSmallIcon(R.drawable.mascot)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setLargeIcon(bitmap)
                    .setContentIntent(intent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setContentText(message)


            notificationManager.notify(uniId, notificationBuilder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "SuperBoard"
        const val CHANNEL_NAME = "SuperBoard"
        const val CHANNEL_DESCRIPTION = "SuperBoard Notification"
        const val MAIN_CLASS = "com.ssafy.superboard.MainActivity"
        const val FCM_KEY = "MESSAGE"
        const val TITLE = "슈퍼보드"
    }
}
