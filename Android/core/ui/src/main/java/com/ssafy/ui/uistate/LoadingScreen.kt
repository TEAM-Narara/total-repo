package com.ssafy.ui.uistate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ssafy.designsystem.values.Gray

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Gray.copy(alpha = 0.7f))
            .clickable(false) { }
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}
