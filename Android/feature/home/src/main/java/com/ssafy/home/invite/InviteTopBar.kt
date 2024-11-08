package com.ssafy.home.invite

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextXLarge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteTopBar(popBackToHome: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
        navigationIcon = {
            IconButton(onClick = { popBackToHome() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "탐색 창",
                    tint = Color.White
                )
            }
        },
        title = {
            Text(text = "멤버 초대", fontSize = TextXLarge, color = Color.White)
        }
    )
}