package com.ssafy.board.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.ssafy.board.board.components.card.CardItem
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ListData
import com.ssafy.board.board.data.ReorderCardData
import com.ssafy.board.board.handleLazyListScroll
import com.ssafy.designsystem.component.ListItem
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingXSmall
import kotlinx.coroutines.launch

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    listData: ListData,
    reorderState: ReorderState<ReorderCardData>,
    cardCollections: Map<Long, MutableState<List<ReorderCardData>>>,
    onTitleChange: (String) -> Unit = {},
    onCardReordered: (Long, Long, Long?, Long?) -> Unit = { _, _, _, _ -> },
    navigateToCardScreen: (Long) -> Unit = {},
    addCard: (Long, String) -> Unit = { _, _ -> },
    addPhoto: () -> Unit = {},
    onFocus: (Long) -> Unit = {},
) {
    val cardLazyListState = rememberLazyListState()
    val collectionState = cardCollections[listData.id] ?: return
    val collection = collectionState.value

    val scope = rememberCoroutineScope()

    ListItem(
        modifier = modifier,
        title = listData.name,
        onTitleChange = onTitleChange,
        addCard = { cardName -> addCard(listData.id, cardName) },
        addPhoto = addPhoto,
        isWatching = listData.isWatching,
        onFocus = { onFocus(listData.id) }
    ) {
        LazyColumn(
            state = cardLazyListState,
            verticalArrangement = Arrangement.spacedBy(PaddingMedium),
            contentPadding = PaddingValues(vertical = PaddingDefault)
        ) {
            if (collection.isEmpty()) item(key = "empty-${listData.id}") {
                ReorderableItem(
                    enabled = false,
                    state = reorderState,
                    key = "empty-${listData.id}",
                    data = ReorderCardData(CardData()),
                    dropStrategy = DropStrategy.CenterDistance,
                    onDragEnter = { state ->
                        collectionState.value = collection.toMutableList().apply {
                            val targetIndex = 0

                            if (!remove(state.data)) {
                                cardCollections[state.data.listId]?.let {
                                    it.value = it.value.toMutableList().apply {
                                        remove(state.data)
                                    }
                                    onFocus(listData.id)
                                }
                            }

                            add(targetIndex, state.data.apply { this.listId = listData.id })

                            scope.launch {
                                handleLazyListScroll(
                                    lazyListState = cardLazyListState,
                                    dropIndex = targetIndex,
                                )
                            }
                        }
                    },
                    onDrop = { state ->
                        val cardId = state.data.cardData.id
                        val targetListId = state.data.listId ?: return@ReorderableItem
                        val index = collection.indexOf(state.data)
                        val prevCardId = if (index <= 0) null else collection[index - 1].cardData.id
                        val nextCardId =
                            if (index < 0 || index >= collection.size - 1) null else collection[index + 1].cardData.id

                        onCardReordered(cardId, targetListId, prevCardId, nextCardId)
                    }
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(PaddingXSmall)
                            .fillMaxWidth()
                    )
                }
            }

            items(collection, key = { it.cardData.id }) { cardData ->
                ReorderableItem(
                    state = reorderState,
                    key = cardData.cardData.id,
                    data = cardData,
                    dropStrategy = DropStrategy.CenterDistance,
                    onDragEnter = { state ->
                        collectionState.value = collection.toMutableList().apply {
                            val targetIndex = indexOf(cardData)
                            if (targetIndex == -1) return@apply

                            if (!remove(state.data)) {
                                cardCollections[state.data.listId]?.let {
                                    it.value = it.value.toMutableList().apply {
                                        remove(state.data)
                                    }
                                    onFocus(listData.id)
                                }
                            }

                            add(targetIndex, state.data.apply { this.listId = listData.id })

                            scope.launch {
                                handleLazyListScroll(
                                    lazyListState = cardLazyListState,
                                    dropIndex = targetIndex,
                                )
                            }
                        }
                    },
                    onDrop = { state ->
                        val cardId = state.data.cardData.id
                        val targetListId = state.data.listId ?: return@ReorderableItem
                        val index = collection.indexOf(state.data)
                        val prevCardId = if (index <= 0) null else collection[index - 1].cardData.id
                        val nextCardId =
                            if (index < 0 || index >= collection.size - 1) null else collection[index + 1].cardData.id

                        onCardReordered(cardId, targetListId, prevCardId, nextCardId)
                    },
                ) {
                    CardItem(
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = if (isDragging) 0f else 1f
                            }
                            .shadow(
                                if (isDragging) ElevationLarge else 0.dp,
                                shape = RoundedCornerShape(CornerMedium),
                            ),
                        cardData = cardData,
                        onClick = { navigateToCardScreen(cardData.cardData.id) },
                    )
                }
            }
        }
    }
}