package com.ssafy.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.ssafy.board.data.BoardData
import com.ssafy.board.data.CardData
import com.ssafy.board.data.ListData
import com.ssafy.board.data.ReorderCardData
import com.ssafy.board.data.toReorderCardData
import com.ssafy.board.handleLazyListScroll
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationLarge
import com.ssafy.designsystem.values.PaddingDefault
import kotlinx.coroutines.launch

@Composable
fun BoardItem(
    modifier: Modifier = Modifier,
    boardData: BoardData,
    onListTitleChanged: () -> Unit,
    onListReordered: () -> Unit,
    onCardReordered: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val listDndState = rememberReorderState<ListData>()
    var listCollection by remember { mutableStateOf(boardData.listCollection) }
    val listLazyListState = rememberLazyListState()

    val cardDndState = rememberReorderState<ReorderCardData>()
    val cardCollections = mutableMapOf<String, MutableState<List<ReorderCardData>>>().apply {
        boardData.listCollection.forEach { listData ->
            this[listData.id] = remember {
                mutableStateOf(listData.cardCollection.map {
                    it.toReorderCardData(listData.id)
                })
            }
        }
    }

    // TODO : card의 onLongPressed가 내려오는 문제 해결
    ReorderContainer(state = listDndState, modifier = modifier.padding(vertical = PaddingDefault)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(PaddingDefault)) {
            item { Spacer(modifier = Modifier) }
            items(listCollection, key = { it.id }) { listData ->
                ReorderableItem(
                    state = listDndState,
                    key = listData.id,
                    data = listData,
                    zIndex = 1f,
                    dropStrategy = DropStrategy.CenterDistance,
                    dragAfterLongPress = true,
                    onDragEnter = { state ->
                        listCollection = listCollection.toMutableList().apply {
                            val index = indexOf(listData)
                            if (index == -1) return@apply

                            remove(state.data)
                            add(index, state.data)

                            scope.launch {
                                handleLazyListScroll(
                                    lazyListState = listLazyListState,
                                    dropIndex = index,
                                )
                            }
                        }
                    },
                    onDrop = { onListReordered() },
                    draggableContent = {
                        ListItem(
                            modifier = Modifier.shadow(
                                ElevationLarge,
                                shape = RoundedCornerShape(CornerMedium),
                            ),
                            listData = listData,
                            reorderState = cardDndState,
                            cardCollections = cardCollections,
                        )
                    }
                ) {
                    ListItem(
                        modifier = Modifier.graphicsLayer { alpha = if (isDragging) 0f else 1f },
                        listData = listData,
                        reorderState = cardDndState,
                        cardCollections = cardCollections,
                        onTitleChange = { onListTitleChanged() },
                        onCardReordered = { onCardReordered() },
                        addCard = { },
                        addPhoto = { },
                    )
                }
            }
            item { Spacer(modifier = Modifier) }
        }
    }
}

@Preview
@Composable
private fun BoardItemPreview() {
    BoardItem(
        boardData = BoardData(
            id = "board 1",
            title = "title",
            listCollection = (1..3).map { listData ->
                ListData(
                    id = "list $listData",
                    title = listData.toString(),
                    cardCollection = (1..3).map { cardData ->
                        CardData(
                            id = "card $listData$cardData",
                            title = cardData.toString()
                        )
                    },
                    isWatching = true
                )
            }
        ),
        onListTitleChanged = { },
        onListReordered = { },
        onCardReordered = { }
    )
}