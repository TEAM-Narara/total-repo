package com.ssafy.notification

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Notification

fun NavGraphBuilder.notificationScreen(
    popBack: () -> Unit,
) {
    composable<Notification> {
        NotificationScreen(
            popBackStack = popBack
        )
    }
}
