package com.ssafy.ui.uistate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ssafy.designsystem.values.Black
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.TextLarge

@Composable
fun LoadingScreen(modifier: Modifier = Modifier, text: String? = null) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Gray.copy(alpha = 0.7f))
            .clickable(false) { }
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PaddingSmall)
        ) {
            CircularProgressIndicator()

            text?.let { loadingText ->
                Text(
                    text = loadingText,
                    color = Black,
                    fontSize = TextLarge
                )
            }
        }
    }
}