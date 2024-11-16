package com.ssafy.home.home

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    title: String = "Work Space",
    onDrawerClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White
        ),
        navigationIcon = {
            IconButton(onClick = onDrawerClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "탐색 창"
                )
            }
        },
        title = {
            Text(text = title, fontSize = TextMedium)
        },
        actions = {
            IconButton(
                onClick = { onSearchClick() },
                modifier = Modifier.then(Modifier.size(IconLarge))
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "검색 창")
            }

            IconButton(
                onClick = { onAlarmClick() },
                modifier = Modifier.then(Modifier.size(IconLarge))
            ) {
                Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "알람 창")
            }

            IconButton(
                onClick = { onMenuClick() },
                modifier = Modifier.then(Modifier.size(IconLarge))
            ) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "더보기")
            }

        }
    )
}

@Composable
@Preview
fun TopBarPreview() {
    MainTopBar(onMenuClick = {}, onSearchClick = {}, onAlarmClick = {}, onDrawerClick = {})
}