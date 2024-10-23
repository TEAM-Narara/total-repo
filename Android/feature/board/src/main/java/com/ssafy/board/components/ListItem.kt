package com.ssafy.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import com.mohamedrejeb.compose.dnd.annotation.ExperimentalDndApi
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.ssafy.board.data.ListData
import com.ssafy.board.data.ReorderCardData
import com.ssafy.board.handleLazyListScroll
import com.ssafy.designsystem.component.ListItem
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import kotlinx.coroutines.launch

@OptIn(ExperimentalDndApi::class)
@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    listData: ListData,
    reorderState: ReorderState<ReorderCardData>,
    cardCollections: Map<String, MutableState<List<ReorderCardData>>>,
    onTitleChange: (String) -> Unit = {},
    onCardReordered: () -> Unit = {},
    addCard: () -> Unit = {},
    addPhoto: () -> Unit = {},
    onDragEnter: () -> Unit = {},
) {
    val cardLazyListState = rememberLazyListState()
    val collectionState = cardCollections[listData.id] ?: return
    val collection = collectionState.value

    val scope = rememberCoroutineScope()

    ReorderContainer(state = reorderState) {
        ListItem(
            modifier = modifier.dropTarget(
                key = listData.id,
                state = reorderState.dndState,
                dropAnimationEnabled = false,
                onDragEnter = { state ->
                    collectionState.value = collection.toMutableList().apply {
                        if (!remove(state.data)) {
                            cardCollections[state.data.listId]?.let {
                                it.value = it.value.toMutableList().apply {
                                    remove(state.data)
                                }
                            }
                        }

                        add(state.data.apply { this.listId = listData.id })

                        onDragEnter()
                    }
                },
                onDrop = { onCardReordered() },
            ),
            title = listData.title,
            onTitleChange = onTitleChange,
            addCard = addCard,
            addPhoto = addPhoto,
            isWatching = listData.isWatching,
        ) {
            LazyColumn(
                state = cardLazyListState,
                verticalArrangement = Arrangement.spacedBy(PaddingMedium),
                contentPadding = PaddingValues(vertical = PaddingDefault)
            ) {
                items(collection, key = { it.id }) { cardData ->
                    ReorderableItem(
                        state = reorderState,
                        key = cardData.id,
                        data = cardData,
                        zIndex = 1f,
                        dropStrategy = DropStrategy.CenterDistance,
                        dragAfterLongPress = true,
                        onDragEnter = { state ->
                            collectionState.value = collection.toMutableList().apply {
                                val index = indexOf(cardData)
                                if (index == -1) return@apply

                                if (!remove(state.data)) {
                                    cardCollections[state.data.listId]?.let {
                                        it.value = it.value.toMutableList().apply {
                                            remove(state.data)
                                        }
                                    }
                                }

                                add(index, state.data.apply { this.listId = listData.id })

                                scope.launch {
                                    handleLazyListScroll(
                                        lazyListState = cardLazyListState,
                                        dropIndex = index,
                                        indexOffset = 1,
                                    )
                                }

                                onDragEnter()
                            }
                        },
                        onDrop = { onCardReordered() },
                        draggableContent = {
                            CardItem(
                                modifier = Modifier.shadow(
                                    ElevationLarge,
                                    shape = RoundedCornerShape(CornerMedium),
                                ),
                                cardData = cardData,
                            )
                        }
                    ) {
                        CardItem(
                            modifier = Modifier.graphicsLayer {
                                alpha = if (isDragging) 0f else 1f
                            },
                            cardData = cardData,
                        )
                    }
                }
            }
        }
    }
}