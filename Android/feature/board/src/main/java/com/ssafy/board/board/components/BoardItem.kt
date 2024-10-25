package com.ssafy.board.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.ssafy.board.board.data.BoardData
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ListData
import com.ssafy.board.board.data.ReorderCardData
import com.ssafy.board.board.data.toReorderCardData
import com.ssafy.board.board.handleLazyListScrollToCenter
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationDefault
import com.ssafy.designsystem.values.ElevationLarge
import com.ssafy.designsystem.values.ListWidth
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White
import kotlinx.coroutines.launch

@Composable
fun BoardItem(
    modifier: Modifier = Modifier,
    boardData: BoardData,
    onListTitleChanged: () -> Unit,
    onListReordered: () -> Unit,
    onCardReordered: () -> Unit,
    addList: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val listDndState = rememberReorderState<ListData>(dragAfterLongPress = true)
    var listCollection by remember { mutableStateOf(boardData.listCollection) }
    val listLazyListState = rememberLazyListState()

    val cardDndState = rememberReorderState<ReorderCardData>(dragAfterLongPress = true)
    val cardCollections = mutableMapOf<Long, MutableState<List<ReorderCardData>>>().apply {
        boardData.listCollection.forEach { listData ->
            this[listData.id] = remember {
                mutableStateOf(listData.cardCollection.map {
                    it.toReorderCardData(listData.id)
                })
            }
        }
    }

    // TODO : card의 onLongPressed가 내려오는 문제 해결
    ReorderContainer(state = listDndState) {
        LazyRow(
            state = listLazyListState,
            horizontalArrangement = Arrangement.spacedBy(PaddingDefault),
            modifier = modifier.padding(vertical = PaddingDefault),
            contentPadding = PaddingValues(horizontal = PaddingDefault)
        ) {
            items(listCollection, key = { it.id }) { listData ->
                ReorderableItem(
                    state = listDndState,
                    key = listData.id,
                    data = listData,
                    dropStrategy = DropStrategy.CenterDistance,
                    onDragEnter = { state ->
                        listCollection = listCollection.toMutableList().apply {
                            val index = indexOf(listData)
                            if (index == -1) return@apply

                            remove(state.data)
                            add(index, state.data)

                            scope.launch {
                                handleLazyListScrollToCenter(
                                    lazyListState = listLazyListState,
                                    dropIndex = index,
                                )
                            }
                        }
                    },
                    onDrop = { onListReordered() },
                ) {
                    ListItem(
                        modifier = Modifier
                            .graphicsLayer { alpha = if (isDragging) 0f else 1f }
                            .shadow(
                                if (isDragging) ElevationLarge else 0.dp,
                                shape = RoundedCornerShape(CornerMedium),
                            ),
                        listData = listData,
                        reorderState = cardDndState,
                        cardCollections = cardCollections,
                        onTitleChange = { onListTitleChanged() },
                        onCardReordered = { onCardReordered() },
                        addCard = { },
                        addPhoto = { },
                        onListChanged = { listId ->
                            scope.launch {
                                handleLazyListScrollToCenter(
                                    lazyListState = listLazyListState,
                                    dropIndex = listCollection.indexOfFirst { it.id == listId },
                                )
                            }
                        }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.width(ListWidth),
                    shape = RoundedCornerShape(CornerMedium),
                    colors = CardDefaults.cardColors().copy(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = ElevationDefault),
                    onClick = addList
                ) {
                    Text(
                        text = "+ Add List",
                        modifier = Modifier.padding(PaddingDefault),
                        fontSize = TextMedium,
                    )
                }
            }
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
            listCollection = (1..1).map { listData ->
                ListData(
                    id = listData.toLong(),
                    title = listData.toString(),
                    cardCollection = (1..3).map { cardData ->
                        CardData(
                            id = (listData * 100 + cardData).toLong(),
                            title = cardData.toString()
                        )
                    },
                    isWatching = true
                )
            }
        ),
        onListTitleChanged = { },
        onListReordered = { },
        onCardReordered = { },
        addList = {}
    )
}