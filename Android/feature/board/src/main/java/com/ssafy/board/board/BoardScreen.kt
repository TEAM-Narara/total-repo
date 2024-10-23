package com.ssafy.board.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.board.board.components.BoardItem
import com.ssafy.board.board.components.TopAppBar
import com.ssafy.board.board.data.BoardData
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ListData
import com.ssafy.ui.uistate.UiState

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    popBack: () -> Unit,
    navigateToFilterScreen: () -> Unit,
    navigateToNotificationScreen: () -> Unit,
    navigateToBoardMenuScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState.run {
        when (this) {
            UiState.Loading -> {
                BoardLoadingScreen(
                    modifier = modifier,
                    popBack = popBack,
                    onNotificationPressed = navigateToNotificationScreen,
                )
            }

            is UiState.Success -> BoardScreen(
                modifier = modifier,
                boardData = this.data,
                onBoardTitleChanged = viewModel::updateBoardTitle,
                onListTitleChanged = viewModel::updateListTitle,
                onCardReordered = viewModel::updateCardOrder,
                onListReordered = viewModel::updateListOrder,
                onFilterPressed = navigateToFilterScreen,
                onNotificationPressed = navigateToNotificationScreen,
                onMorePressed = navigateToBoardMenuScreen,
                popBack = popBack,
            )

            is UiState.Error -> {
                BoardErrorScreen(
                    modifier = modifier,
                    popBack = popBack,
                    onNotificationPressed = navigateToNotificationScreen,
                )
            }
        }
    }
}

@Composable
fun BoardLoadingScreen(
    modifier: Modifier = Modifier,
    popBack: () -> Unit,
    onNotificationPressed: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = "",
                onBackPressed = popBack,
                onBoardTitleChanged = { },
                onFilterPressed = { },
                onNotificationPressed = onNotificationPressed,
                onMorePressed = { },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun BoardErrorScreen(
    modifier: Modifier = Modifier,
    popBack: () -> Unit,
    onNotificationPressed: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = "",
                onBackPressed = popBack,
                onBoardTitleChanged = { },
                onFilterPressed = { },
                onNotificationPressed = onNotificationPressed,
                onMorePressed = { },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(text = "Error", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun BoardScreen(
    modifier: Modifier = Modifier,
    boardData: BoardData,
    onBoardTitleChanged: () -> Unit,
    onListTitleChanged: () -> Unit,
    onCardReordered: () -> Unit,
    onListReordered: () -> Unit,
    onFilterPressed: () -> Unit,
    onNotificationPressed: () -> Unit,
    onMorePressed: () -> Unit,
    popBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = boardData.title,
                onBackPressed = popBack,
                onBoardTitleChanged = { onBoardTitleChanged() },
                onFilterPressed = onFilterPressed,
                onNotificationPressed = onNotificationPressed,
                onMorePressed = onMorePressed,
            )
        },
    ) { paddingValues ->
        BoardItem(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            boardData = boardData,
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
        boardData = BoardData(
            id = "board 1",
            title = "title",
            listCollection = (1..10).map { listData ->
                ListData(
                    id = "list $listData",
                    title = listData.toString(),
                    cardCollection = (1..3).map { cardData ->
                        CardData(
                            id = "card $listData$cardData",
                            title = cardData.toString(),
                        )
                    },
                    isWatching = true
                )
            }
        ),
        onBoardTitleChanged = {},
        onListTitleChanged = {},
        onCardReordered = {},
        onListReordered = {},
        onFilterPressed = {},
        onNotificationPressed = {},
        onMorePressed = {},
        popBack = {}
    )
}

@Preview
@Composable
private fun BoardLoadingScreenPreview() {
    BoardLoadingScreen(
        onNotificationPressed = {},
        popBack = {}
    )
}

@Preview
@Composable
private fun BoardErrorScreenPreview() {
    BoardErrorScreen(
        onNotificationPressed = {},
        popBack = {}
    )
}