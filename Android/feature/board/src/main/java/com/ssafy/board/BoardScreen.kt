package com.ssafy.board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.ssafy.board.components.ListItem
import com.ssafy.board.components.TopAppBar
import com.ssafy.board.data.BoardData
import com.ssafy.board.data.CardData
import com.ssafy.board.data.ListData
import com.ssafy.board.data.ReorderCardData
import com.ssafy.board.data.toReorderCardData
import com.ssafy.designsystem.values.PaddingDefault

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

@Composable
private fun BoardScreen(
    modifier: Modifier = Modifier,
    uiState: BoardUiState,
    onBoardTitleChanged: () -> Unit,
    onListTitleChanged: () -> Unit,
    onCardReordered: () -> Unit,
    popBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = uiState.boardData.title,
                onBackPressed = popBack,
                onBoardTitleChanged = { onBoardTitleChanged() },
                onFilterPressed = {},
                onNotificationPressed = {},
                onMorePressed = {},
            )
        },
    ) { paddingValues ->
        BoardScreenBody(
            modifier = Modifier.padding(paddingValues),
            boardData = uiState.boardData,
            onListTitleChanged = onListTitleChanged,
            onCardReordered = onCardReordered,
        )
    }
}

@Composable
private fun BoardScreenBody(
    modifier: Modifier = Modifier,
    boardData: BoardData,
    onListTitleChanged: () -> Unit,
    onCardReordered: () -> Unit
) {
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

    LazyRow(
        modifier = modifier.padding(top = PaddingDefault),
        horizontalArrangement = Arrangement.spacedBy(PaddingDefault)
    ) {
        item { Spacer(modifier = Modifier) }
        items(boardData.listCollection, key = { it.id }) { listData ->
            ListItem(
                listData = listData,
                reorderState = cardDndState,
                cardCollections = cardCollections,
                onTitleChange = { onListTitleChanged() },
                onCardReordered = { onCardReordered() },
                addCard = { },
                addPhoto = { },
            )
        }
        item { Spacer(modifier = Modifier) }
    }
}

@Preview
@Composable
private fun BoardScreenPreview() {
    BoardScreen(
        uiState = BoardUiState(
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
                        }
                    )
                }
            )
        ),
        onBoardTitleChanged = {},
        onListTitleChanged = {},
        onCardReordered = {},
        popBack = {}
    )
}