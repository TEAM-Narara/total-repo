package com.ssafy.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Typeface
import android.media.RingtoneManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ssafy.designsystem.R
import com.ssafy.fcm.data.BoardMessage
import com.ssafy.fcm.data.CardMessage
import com.ssafy.fcm.data.FcmDirection
import com.ssafy.fcm.data.HomeMessage
import com.ssafy.fcm.data.WorkspaceMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class SuperBoardFcmService : FirebaseMessagingService() {

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { updateFcmTokenUseCase(token) }
                .onFailure { it.printStackTrace() }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleMessage(remoteMessage)
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title.toString().toSpanString()
        val body = remoteMessage.notification?.body.toString().toSpanString()
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
        sendNotification(pendingIntent, title, body, uniId)
    }

    private fun convertToMessage(goTo: FcmDestination, data: Map<String, String>): FcmDirection {
        val gson = Gson()
        val jsonString = gson.toJson(data)

        return when (goTo) {
            FcmDestination.HOME -> gson.fromJson(jsonString, HomeMessage::class.java)
            FcmDestination.WORKSPACE -> gson.fromJson(jsonString, WorkspaceMessage::class.java)
            FcmDestination.BOARD -> gson.fromJson(jsonString, BoardMessage::class.java)
            FcmDestination.CARD -> gson.fromJson(jsonString, CardMessage::class.java)
        }
    }

    private fun setPendingIntent(fcmDirection: FcmDirection, uniId: Int): PendingIntent {
        val channelId = CHANNEL_ID
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelName = CHANNEL_NAME
            val channelDescription = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, Class.forName(MAIN_CLASS)).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(FCM_KEY, fcmDirection)
        }

        return PendingIntent.getActivity(
            this, uniId, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun sendNotification(
        intent: PendingIntent,
        title: SpannableString,
        body: SpannableString,
        uniId: Int
    ) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.mascot)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(intent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        notificationManager.notify(uniId, notificationBuilder.build())
    }

    private fun String.toSpanString(): SpannableString {
        val spannableString = SpannableString(this.replace("**", ""))
        val pattern = "\\*\\*(.*?)\\*\\*".toRegex()

        pattern.findAll(this).forEach { matchResult ->
            val boldText = matchResult.groupValues[1]
            val start = this.indexOf(boldText)
            val end = start + boldText.length
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }

    companion object {
        const val CHANNEL_ID = "SuperBoard"
        const val CHANNEL_NAME = "SuperBoard"
        const val CHANNEL_DESCRIPTION = "SuperBoard Notification"
        const val MAIN_CLASS = "com.ssafy.superboard.MainActivity"
        const val FCM_KEY = "MESSAGE"
    }
}
