package com.ssafy.card.label.components

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
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "라벨",
    onClosePressed: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title, fontSize = TextMedium) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White
        ),
        navigationIcon = {
            IconButton(onClick = onClosePressed) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "뒤로가기"
                )
            }
        }
    )
}