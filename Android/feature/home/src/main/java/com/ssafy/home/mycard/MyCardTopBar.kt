package com.ssafy.home.mycard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCardTopBar(
    modifier: Modifier = Modifier,
    title: String = "My Cards",
    onNavigateClick: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White
        ),
        navigationIcon = {
            IconButton(onClick = onNavigateClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "뒤로 가기"
                )
            }
        },
        title = {
            Text(text = title, fontSize = TextMedium)
        },
    )
}