package com.ssafy.board

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.board.components.BoardItem
import com.ssafy.board.components.TopAppBar
import com.ssafy.board.data.BoardData
import com.ssafy.board.data.CardData
import com.ssafy.board.data.ListData
import com.ssafy.model.search.SearchParameters

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    searchParameters: SearchParameters,
    popBack: () -> Unit,
    moveBoardSetting: () -> Unit,
    moveToBoardSearch: (SearchParameters) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BoardScreen(
        modifier = modifier,
        uiState = uiState,
        onFilterPressed = { /*moveToBoardSearch(viewModel.searchParameters)*/
            moveToBoardSearch(SearchParameters())
        },
        onBoardTitleChanged = viewModel::updateBoardTitle,
        onListTitleChanged = viewModel::updateListTitle,
        onCardReordered = viewModel::updateCardOrder,
        onListReordered = viewModel::updateListOrder,
        popBack = popBack,
        moveBoardSetting = moveBoardSetting
    )
}

@Composable
private fun BoardScreen(
    modifier: Modifier = Modifier,
    uiState: BoardUiState,
    onFilterPressed: () -> Unit,
    onBoardTitleChanged: () -> Unit,
    onListTitleChanged: () -> Unit,
    onCardReordered: () -> Unit,
    onListReordered: () -> Unit,
    popBack: () -> Unit,
    moveBoardSetting: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = uiState.boardData.title,
                onBackPressed = popBack,
                onBoardTitleChanged = { onBoardTitleChanged() },
                onFilterPressed = onFilterPressed,
                onNotificationPressed = {},
                onMorePressed = { moveBoardSetting() },
            )
        },
    ) { paddingValues ->
        BoardItem(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            boardData = uiState.boardData,
            onListTitleChanged = onListTitleChanged,
            onCardReordered = onCardReordered,
            onListReordered = onListReordered,
        )
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
                        },
                        isWatching = true
                    )
                }
            )
        ),
        onBoardTitleChanged = {},
        onListTitleChanged = {},
        onCardReordered = {},
        onListReordered = {},
        popBack = {},
        onFilterPressed = {},
        moveBoardSetting = {}
    )
}