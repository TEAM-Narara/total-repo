package com.ssafy.board.updateboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateBoardTopBar(
    modifier: Modifier = Modifier,
    onNavigateClick: () -> Unit,
    title: String = "보드 수정",
    containerColor: Color = Primary,
    contentColor: Color = White
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor
        ),
        title = {
            Text(text = title, fontSize = TextMedium)
        },
        navigationIcon = {
            IconButton(onClick = onNavigateClick) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "뒤로 가기")
            }
        }
    )
}