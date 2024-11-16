package com.ssafy.notification

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.designsystem.values.IconXLarge
import com.ssafy.designsystem.values.LightGray
import com.ssafy.notification.component.NotificationItem
import com.ssafy.notification.component.TopAppBar
import com.ssafy.notification.fcm.SuperBoardFcmService.Companion.FCM_KEY
import com.ssafy.notification.fcm.SuperBoardFcmService.Companion.MAIN_CLASS
import com.ssafy.notification.fcm.data.FcmMessage
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notificationList by viewModel.notificationList.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.resetUiState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = "알림",
                onClosePressed = popBackStack,
                onAllCheckPressed = {},
                onMorePressed = {},
            )
        }
    ) { paddingValues ->

        NotificationScreen(
            modifier = Modifier.padding(paddingValues),
            notificationList = notificationList,
            moveToDetail = { notification ->
                val intent = Intent(context, Class.forName(MAIN_CLASS)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(FCM_KEY, notification)
                }
                context.startActivity(intent)
            }
        )
    }

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let {
            ErrorScreen(
                errorMessage = it,
                afterAction = popBackStack
            )
        }

        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    notificationList: List<FcmMessage>,
    moveToDetail: (FcmMessage) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(notificationList.size) { index ->
            val notification = notificationList[index]
            NotificationItem(
                content = notification.title?.replace("*", "") ?: "",
                date = notification.time ?: 0,
                profile = {
                    AsyncImage(
                        modifier = Modifier
                            .size(IconXLarge)
                            .clip(CircleShape),
                        model = notification.manOfActionUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(Icons.Default.AccountCircle)
                    )
                },
                onClick = { moveToDetail(notification) }
            )

            HorizontalDivider(color = LightGray)
        }
    }
}

@Preview
@Composable
private fun NotificationScreenPreview() {

}