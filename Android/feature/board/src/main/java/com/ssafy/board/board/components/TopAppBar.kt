package com.ssafy.board.board.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.component.IconButton
import com.ssafy.designsystem.values.Black
import com.ssafy.designsystem.values.IconMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onBoardNameChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
    onFilterPressed: () -> Unit,
    onNotificationPressed: () -> Unit,
    onMorePressed: () -> Unit,
    tint: Color = Black
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
            EditableText(text = title, onInputFinished = onBoardNameChanged, textColor = tint)
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "",
                    modifier = Modifier.size(IconMedium),
                    tint = tint
                )
            }
        },
        actions = {
            IconButton(
                imageVector = Icons.Default.FilterList,
                onClick = onFilterPressed,
                tint = tint
            )
            IconButton(
                imageVector = Icons.Default.NotificationsNone,
                onClick = onNotificationPressed,
                tint = tint
            )
            IconButton(
                imageVector = Icons.Default.MoreVert,
                onClick = onMorePressed,
                tint = tint
            )
        }
    )
}