package com.ssafy.board.board

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.ssafy.board.board.components.AddListButton
import com.ssafy.board.board.components.ListItem
import com.ssafy.board.board.components.TopAppBar
import com.ssafy.board.board.data.BoardData
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ListData
import com.ssafy.board.board.data.ReorderCardData
import com.ssafy.board.board.data.toReorderCardData
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationLarge
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.model.search.SearchParameters
import kotlinx.coroutines.launch

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    searchParameters: SearchParameters,
    popBack: () -> Unit,
    navigateToFilterScreen: (SearchParameters) -> Unit,
    navigateToNotificationScreen: () -> Unit,
    navigateToBoardMenuScreen: () -> Unit,
    navigateToCardScreen: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boardData by viewModel.boardData.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = boardData?.title ?: "",
                onBackPressed = popBack,
                onBoardTitleChanged = { viewModel.updateBoardTitle() },
                onFilterPressed = { navigateToFilterScreen(searchParameters) },
                onNotificationPressed = navigateToNotificationScreen,
                onMorePressed = { navigateToBoardMenuScreen() },
            )
        },
    ) { paddingValues ->
        boardData?.let {
            BoardScreen(
                modifier = Modifier.padding(paddingValues),
                boardData = it,
                onListTitleChanged = viewModel::updateListTitle,
                onCardReordered = viewModel::updateCardOrder,
                onListReordered = viewModel::updateListOrder,
                navigateToCardScreen = navigateToCardScreen,
                addList = viewModel::addList,
                addCard = viewModel::addCard,
                addPhoto = viewModel::addPhoto
            )
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray.copy(alpha = 0.7f))
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (uiState.isError && uiState.errorMessage != null) {
        Toast.makeText(
            LocalContext.current,
            uiState.errorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }
}


@Composable
private fun BoardScreen(
    modifier: Modifier = Modifier,
    boardData: BoardData,
    onListTitleChanged: () -> Unit,
    onCardReordered: () -> Unit,
    onListReordered: () -> Unit,
    navigateToCardScreen: (Long) -> Unit,
    addList: () -> Unit,
    addCard: () -> Unit,
    addPhoto: () -> Unit,
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
        ReorderContainer(state = cardDndState) {
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
                            navigateToCardScreen = { id -> navigateToCardScreen(id) },
                            addCard = addCard,
                            addPhoto = addPhoto,
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
                    AddListButton(onClick = addList)
                }
            }
        }
    }
}

@Preview
@Composable
private fun BoardScreenPreview() {
    BoardScreen(
        boardData = BoardData(
            id = 1,
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
        onListTitleChanged = {},
        onCardReordered = {},
        onListReordered = {},
        navigateToCardScreen = {},
        addList = {},
        addCard = {},
        addPhoto = {}
    )
}