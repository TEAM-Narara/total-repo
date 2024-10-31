package com.ssafy.login.login.github

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer

@Composable
fun GitHubOauthEffect(
    onSuccessToGetCode: (String) -> Unit,
    onFailToGetCode: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    DisposableEffect(key1 = activity) {
        val fail = "GitHub 데이터 연동 실패!"
        val listener = Consumer<Intent> { intent ->
            val uri = intent.data ?: return@Consumer onFailToGetCode(fail)
            val code = uri.getQueryParameter("code") ?: return@Consumer onFailToGetCode(fail)
            onSuccessToGetCode(code)
        }

        activity?.addOnNewIntentListener(listener)

        onDispose {
            activity?.removeOnNewIntentListener(listener)
        }
    }

}
