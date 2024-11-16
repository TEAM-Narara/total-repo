package com.ssafy.notification.notification

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hail
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.LightGray
import com.ssafy.notification.notification.component.NotificationItem
import com.ssafy.notification.notification.component.TopAppBar
import com.ssafy.notification.notification.data.NotificationData
import com.ssafy.notification.notification.data.toContentString
import java.time.ZonedDateTime

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notificationList by viewModel.notificationList.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = "알림",
                onClosePressed = popBackStack,
                onAllCheckPressed = viewModel::checkAllNotification,
                onMorePressed = {},
            )
        }
    ) { paddingValues ->
        notificationList?.let {
            NotificationScreen(
                modifier = Modifier.padding(paddingValues),
                notificationList = it
            )
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray.copy(alpha = 0.7f))
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (uiState.isError && uiState.errorMessage != null) {
        Toast.makeText(
            LocalContext.current,
            uiState.errorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun NotificationScreen(modifier: Modifier = Modifier, notificationList: List<NotificationData>) {
    LazyColumn(modifier = modifier) {
        items(notificationList) { notificationList ->
            NotificationItem(
                content = notificationList.toContentString(),
                date = ZonedDateTime.now().toEpochSecond()
            ) {
                Icon(Icons.Default.Hail, modifier = Modifier.fillMaxSize(), contentDescription = "")
            }
            HorizontalDivider(color = LightGray)
        }
    }
}

@Preview
@Composable
private fun NotificationScreenPreview() {

}