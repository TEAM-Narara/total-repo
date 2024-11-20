package com.ssafy.notification.component

import androidx.compose.foundation.layout.size
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
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.TextMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onClosePressed: () -> Unit,
    onAllCheckPressed: () -> Unit,
    onMorePressed: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = { Text(text = title, fontSize = TextMedium) },
        navigationIcon = {
            IconButton(onClick = onClosePressed) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                    modifier = Modifier.size(IconMedium)
                )
            }
        },
//        actions = {
//            IconButton(imageVector = Icons.Default.Checklist, onClick = onAllCheckPressed)
//            IconButton(imageVector = Icons.Default.MoreVert, onClick = onMorePressed)
//        }
    )
}