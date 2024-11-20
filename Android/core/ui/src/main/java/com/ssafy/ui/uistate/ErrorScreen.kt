package com.ssafy.ui.uistate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
    afterAction: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val snackBarHost = remember { SnackbarHostState() }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier.fillMaxSize()
    ) {
        SnackbarHost(hostState = snackBarHost)

        LaunchedEffect(errorMessage) {
            scope.launch {
                snackBarHost.showSnackbar(errorMessage, duration = duration)
                afterAction?.invoke()
            }
        }
    }
}
