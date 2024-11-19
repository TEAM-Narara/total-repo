package com.ssafy.notification.fcm

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import com.ssafy.notification.fcm.data.FcmMessage

@Composable
@Suppress("DEPRECATION")
fun FcmEffect(moveToFcmDirection: (FcmMessage) -> Unit) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // 앱이 켜져 있는 상태에서 알람이 올 때
    DisposableEffect(key1 = activity) {
        val listener = Consumer<Intent> { intent ->
            val direction = intent.getParcelableExtra<FcmMessage?>(SuperBoardFcmService.FCM_KEY)
            direction?.let(moveToFcmDirection)
        }

        activity?.addOnNewIntentListener(listener)

        onDispose {
            activity?.removeOnNewIntentListener(listener)
        }
    }

    // 앱이 꺼져 있어 새 알람이 왔을 때
    activity?.intent?.let {
        val direction = it.getParcelableExtra<FcmMessage?>(SuperBoardFcmService.FCM_KEY)
        direction?.let(moveToFcmDirection)
    }
}
