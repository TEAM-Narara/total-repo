package com.ssafy.card.card.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.designsystem.component.IconText
import com.ssafy.designsystem.values.LabelRed
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardTopBar(
    modifier: Modifier = Modifier,
    title: String = "Card",
    isWatching: Boolean = false,
    onBackPressed: () -> Unit,
    onWatchSelected: (Boolean) -> Unit,
    moveToArchive: () -> Unit,
    moveToDelete: () -> Unit,
    attachments: List<String>,
    heightOffset: Float,
) {
    val (isExpanded, setExpanded) = remember { mutableStateOf(false) }
    val icon = if (isWatching) Icons.Default.VisibilityOff else Icons.Default.Visibility
    val iconText = if (isWatching) "알림 설정 해제" else "알림 설정"

    CardTopImage(
        attachments = attachments,
        heightOffset = heightOffset
    )

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = { Text(text = title, fontSize = TextMedium) },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "뒤로 가기"
                )
            }
        },
        actions = {
            IconButton(onClick = { setExpanded(true) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "더보기"
                )
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { setExpanded(false) },
                containerColor = White
            ) {
                DropdownMenuItem(
                    text = {
                        IconText(
                            icon = icon,
                            text = iconText,
                            fontSize = TextMedium,
                            fontWeight = FontWeight.Normal,
                            space = PaddingXSmall
                        )
                    }, onClick = {
                        setExpanded(false)
                        onWatchSelected(!isWatching)
                    }
                )

                DropdownMenuItem(
                    text = {
                        IconText(
                            icon = Icons.Default.Archive,
                            text = "아카이브 이동",
                            fontSize = TextMedium,
                            fontWeight = FontWeight.Normal,
                            space = PaddingXSmall
                        )
                    }, onClick = {
                        setExpanded(false)
                        moveToArchive()
                    }
                )

                DropdownMenuItem(
                    text = {
                        IconText(
                            icon = Icons.Default.Delete,
                            text = "삭제",
                            fontSize = TextMedium,
                            fontWeight = FontWeight.Normal,
                            space = PaddingXSmall,
                            tint = LabelRed,
                            color = LabelRed
                        )
                    }, onClick = {
                        setExpanded(false)
                        moveToDelete()
                    }
                )
            }
        }
    )
}
