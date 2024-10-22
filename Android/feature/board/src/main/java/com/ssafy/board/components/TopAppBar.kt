package com.ssafy.board.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.component.IconButton
import com.ssafy.designsystem.values.IconMedium
import androidx.compose.material3.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onBoardTitleChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
    onFilterPressed: () -> Unit,
    onNotificationPressed: () -> Unit,
    onMorePressed: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
            EditableText(text = title, onTextChanged = onBoardTitleChanged)
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    modifier = Modifier.size(IconMedium)
                )
            }
        },
        actions = {
            IconButton(imageVector = Icons.Default.FilterList, onClick = onFilterPressed)
            IconButton(imageVector = Icons.Default.NotificationsNone, onClick = onNotificationPressed)
            IconButton(imageVector = Icons.Default.MoreVert, onClick = onMorePressed)
        }
    )
}