package com.ssafy.board.board.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ListData
import com.ssafy.board.board.data.ReorderCardData
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.component.IconButton
import com.ssafy.designsystem.component.IconText
import com.ssafy.designsystem.values.Black
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.ReversePrimary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onBoardNameChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
    onFilterPressed: () -> Unit,
    onNotificationPressed: () -> Unit,
    onMorePressed: () -> Unit,
    tint: Color = Black,
    listReorderState: ReorderState<ListData>,
    onListArchived: (Long) -> Unit,
    cardReorderState: ReorderState<ReorderCardData>,
    onCardArchived: (Long) -> Unit,
) {
    Box(modifier = Modifier.zIndex(0.0f)) {
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

        if (listReorderState.draggedItem?.data != null) ReorderableItem(
            enabled = false,
            modifier = Modifier.align(Alignment.Center),
            state = listReorderState,
            key = "List Archive Item",
            data = ListData(),
            onDrop = {
                listReorderState.draggedItem?.data?.let {
                    onListArchived(it.id)
                }
            }
        ) {
            ArchiveItem(text = "아카이브")
        }

        if (cardReorderState.draggedItem?.data != null) ReorderableItem(
            enabled = false,
            modifier = Modifier.align(Alignment.Center),
            state = cardReorderState,
            key = "Card Archive Item",
            data = ReorderCardData(
                cardData = CardData(
                    id = 0,
                    listId = 0,
                    name = "",
                )
            ),
            onDrop = {
                cardReorderState.draggedItem?.data?.let {
                    onCardArchived(it.cardData.id)
                }
            }
        ) {
            ArchiveItem(text = "아카이브")
        }
    }
}

@Composable
fun ArchiveItem(modifier: Modifier = Modifier, text: String) {
    IconText(
        modifier = modifier
            .background(ReversePrimary, shape = RoundedCornerShape(CornerMedium))
            .padding(PaddingSmall),
        text = text,
        tint = White,
        color = White,
        icon = Icons.Outlined.Archive,
        fontSize = TextMedium
    )
}
