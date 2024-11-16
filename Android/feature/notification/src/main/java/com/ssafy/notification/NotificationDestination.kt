package com.ssafy.notification

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Notification

fun NavGraphBuilder.notificationScreen(
    popBack: () -> Unit,
) {
    composable<Notification> {
        val viwModel: NotificationViewModel = hiltViewModel<NotificationViewModel>().apply {
            LaunchedEffect(Unit) {
                getNotificationList()
            }
        }

        NotificationScreen(
            viewModel = viwModel,
            popBackStack = popBack
        )
    }
}
