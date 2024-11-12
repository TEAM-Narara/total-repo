package com.ssafy.board.board

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
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
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.toColor
import com.ssafy.model.background.Cover
import com.ssafy.model.board.Visibility
import com.ssafy.model.search.SearchParameters
import com.ssafy.model.with.CoverType
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState
import kotlinx.coroutines.launch

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    searchParameters: SearchParameters,
    popBack: () -> Unit,
    navigateToFilterScreen: (SearchParameters) -> Unit,
    navigateToNotificationScreen: () -> Unit,
    navigateToBoardMenuScreen: (boardId: Long, workspaceId: Long) -> Unit,
    navigateToCardScreen: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boardData by viewModel.boardData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.resetUiState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = boardData?.name ?: "",
                onBackPressed = popBack,
                onBoardNameChanged = viewModel::updateBoardName,
                onFilterPressed = { navigateToFilterScreen(searchParameters) },
                onNotificationPressed = navigateToNotificationScreen,
                onMorePressed = {
                    boardData?.let { navigateToBoardMenuScreen(it.id, it.workspaceId) }
                },
            )
        },
    ) { paddingValues ->

        boardData?.let {
            Box(modifier = Modifier.padding(paddingValues)) {
                when (it.cover.type) {

                    CoverType.IMAGE -> {
                        AsyncImage(
                            model = it.cover.value,
                            contentScale = ContentScale.Crop,
                            contentDescription = "Board Cover",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    CoverType.COLOR -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = it.cover.value.toColor())
                        )
                    }

                    else -> {}
                }

                BoardScreen(
                    modifier = Modifier.fillMaxSize(),
                    boardData = it,
                    onListTitleChanged = viewModel::updateListName,
                    onCardReordered = viewModel::updateCardOrder,
                    onListReordered = viewModel::updateListOrder,
                    navigateToCardScreen = navigateToCardScreen,
                    addList = viewModel::addList,
                    addCard = viewModel::addCard,
                    addPhoto = viewModel::addPhoto
                )
            }
        } ?: LoadingScreen()
    }

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}


@Composable
private fun BoardScreen(
    modifier: Modifier = Modifier,
    boardData: BoardData,
    onListTitleChanged: (Long, String) -> Unit,
    onCardReordered: () -> Unit,
    onListReordered: () -> Unit,
    navigateToCardScreen: (Long) -> Unit,
    addList: (String) -> Unit,
    addCard: (Long, String) -> Unit,
    addPhoto: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val listDndState = rememberReorderState<ListData>(dragAfterLongPress = true)
    var listCollection by remember(boardData.listCollection) { mutableStateOf(boardData.listCollection) }
    val listLazyListState = rememberLazyListState()

    val cardDndState = rememberReorderState<ReorderCardData>(dragAfterLongPress = true)
    val cardCollections = mutableMapOf<Long, MutableState<List<ReorderCardData>>>().apply {
        boardData.listCollection.forEach { listData ->
            this[listData.id] = remember(listData) {
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
                modifier = modifier
                    .fillMaxSize()
                    .padding(vertical = PaddingDefault),
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
                            onTitleChange = { onListTitleChanged(listData.id, it) },
                            onCardReordered = { onCardReordered() },
                            navigateToCardScreen = { id -> navigateToCardScreen(id) },
                            addCard = addCard,
                            addPhoto = addPhoto,
                            onFocus = { listId ->
                                scope.launch {
                                    handleLazyListScrollToCenter(
                                        lazyListState = listLazyListState,
                                        dropIndex = listCollection.indexOfFirst { it.id == listId },
                                    )
                                }
                            },
                        )
                    }
                }

                item {
                    AddListButton(addList = addList) {
                        scope.launch {
                            handleLazyListScrollToCenter(
                                lazyListState = listLazyListState,
                                dropIndex = listCollection.size,
                            )
                        }
                    }
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
            name = "title",
            workspaceId = 0,
            cover = Cover(CoverType.COLOR, ""),
            isClosed = false,
            visibility = Visibility.PRIVATE,
            listCollection = (1..1).map { listData ->
                ListData(
                    id = listData.toLong(),
                    name = listData.toString(),
                    myOrder = 1,
                    isArchived = false,
                    cardCollection = (1..3).map { cardData ->
                        CardData(
                            id = (listData * 100 + cardData).toLong(),
                            listId = listData.toLong(),
                            name = cardData.toString(),
                            isWatching = true,
                            isSynced = true,
                            cardLabels = emptyList(),
                            cardMembers = emptyList(),
                            attachment = false,
                            replyCount = 3,
                        )
                    },
                    isWatching = true,
                    isSynced = false,
                )
            }
        ),
        onListTitleChanged = { _, _ -> },
        onCardReordered = {},
        onListReordered = {},
        navigateToCardScreen = {},
        addList = {},
        addCard = { _, _ -> },
        addPhoto = {}
    )
}