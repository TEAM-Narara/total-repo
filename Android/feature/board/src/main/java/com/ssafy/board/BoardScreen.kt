package com.ssafy.board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.compose.dnd.annotation.ExperimentalDndApi
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.ssafy.designsystem.component.CardItem
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.component.IconButton
import com.ssafy.designsystem.component.ListItem
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.RadiusDefault
import kotlinx.coroutines.launch

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    popBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BoardScreen(
        modifier = modifier,
        uiState = uiState,
        onBoardTitleChanged = viewModel::updateBoardTitle,
        onListTitleChanged = viewModel::updateListTitle,
        onCardReordered = viewModel::updateCard,
        popBack = popBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoardScreen(
    modifier: Modifier = Modifier,
    uiState: BoardUiState,
    onBoardTitleChanged: (String) -> Unit,
    onListTitleChanged: (String) -> Unit,
    onCardReordered: (String, CardData) -> Unit,
    popBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    EditableText(text = uiState.title, onTextChanged = onBoardTitleChanged)
                },
                navigationIcon = {
                    IconButton(onClick = popBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            modifier = Modifier.size(IconMedium)
                        )
                    }
                },
                actions = {
                    IconButton(imageVector = Icons.Default.FilterList, onClick = { })
                    IconButton(imageVector = Icons.Default.NotificationsNone, onClick = { })
                    IconButton(imageVector = Icons.Default.MoreVert, onClick = { })
                }
            )
        },
    ) { paddingValues ->
        BoardScreenBody(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onListTitleChanged = onListTitleChanged,
            onCardReordered = onCardReordered,
        )
    }
}

@OptIn(ExperimentalDndApi::class)
@Composable
fun BoardScreenBody(
    modifier: Modifier = Modifier,
    uiState: BoardUiState,
    onListTitleChanged: (String) -> Unit,
    onCardReordered: (String, CardData) -> Unit
) {
    val scope = rememberCoroutineScope()
    val cardDndState = rememberReorderState<ReorderCardData>()
    val reorderMap = mutableMapOf<String, MutableState<List<ReorderCardData>>>().apply {
        uiState.listCollection.forEach { listData ->
            this[listData.id] = remember { mutableStateOf(listData.cardCollection.map { it.toReorderCardData(listData.id) }) }
        }
    }

    LazyRow(
        modifier = modifier.padding(top = PaddingDefault),
        horizontalArrangement = Arrangement.spacedBy(PaddingDefault)
    ) {
        item { Spacer(modifier = Modifier) }
        items(uiState.listCollection) { listData ->
            val cardLazyListState = rememberLazyListState()
            val collection = reorderMap[listData.id]!!.value
            ReorderContainer(state = cardDndState) {
                ListItem(
                    Modifier.dropTarget(
                        key = listData.id,
                        state = cardDndState.dndState,
                        dropAnimationEnabled = false,
                        onDragEnter = { state ->
                            reorderMap[listData.id]!!.value = collection.toMutableList().apply {
                                if(!remove(state.data)) {
                                    reorderMap[state.data.listId]!!.value = reorderMap[state.data.listId]!!.value.toMutableList().apply {
                                        remove(state.data)
                                    }
                                }
                                add(state.data.apply { this.listId = listData.id })
                            }
                        },
                        onDrop = { state ->
                            onCardReordered(listData.id, state.data)
                        }
                    ),
                    title = listData.title,
                    onTitleChange = onListTitleChanged,
                    addCard = { /*TODO*/ },
                    addPhoto = { /*TODO*/ }
                ) {
                    LazyColumn(
                        state = cardLazyListState,
                        verticalArrangement = Arrangement.spacedBy(PaddingMedium)
                    ) {
                        item { Spacer(modifier = Modifier.height(PaddingXSmall)) }
                        items(collection, key = { it.id }) { cardData ->
                            ReorderableItem(
                                state = cardDndState,
                                key = cardData.id,
                                data = cardData,
                                zIndex = 1f,
                                dropStrategy = DropStrategy.CenterDistance,
                                dragAfterLongPress = true,
                                onDragEnter = { state ->
                                    reorderMap[listData.id]!!.value = collection.toMutableList().apply {
                                        val index = indexOf(cardData)
                                        if (index == -1) return@apply

                                        if(!remove(state.data)) {
                                            reorderMap[state.data.listId]!!.value = reorderMap[state.data.listId]!!.value.toMutableList().apply {
                                                remove(state.data)
                                            }
                                        }
                                        add(index, state.data.apply { this.listId = listData.id })

                                        scope.launch {
                                            handleLazyListScroll(
                                                lazyListState = cardLazyListState,
                                                dropIndex = index,
                                            )
                                        }
                                    }
                                },
                                onDrop = { state ->
                                    onCardReordered(listData.id, state.data)
                                },
                                draggableContent = {
                                    CardItem(
                                        title = cardData.title, modifier = Modifier.shadow(
                                            elevation = 20.dp,
                                            shape = RoundedCornerShape(RadiusDefault),
                                        )
                                    )
                                },
                            ) {
                                CardItem(
                                    title = cardData.title,
                                    modifier = Modifier.graphicsLayer {
                                        alpha = if (isDragging) 0f else 1f
                                    })
                            }
                        }
                        item { Spacer(modifier = Modifier.height(PaddingXSmall)) }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier) }
    }
}

@Preview
@Composable
private fun BoardScreenPreview() {
    BoardScreen(
        uiState = BoardUiState(
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
                    }
                )
            }
        ),
        onBoardTitleChanged = {},
        onListTitleChanged = {},
        onCardReordered = { _, _ -> },
        popBack = {}
    )
}