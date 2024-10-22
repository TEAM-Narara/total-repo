package com.ssafy.board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.ssafy.board.components.ListItem
import com.ssafy.board.components.TopAppBar
import com.ssafy.board.data.BoardData
import com.ssafy.board.data.CardData
import com.ssafy.board.data.ListData
import com.ssafy.board.data.ReorderCardData
import com.ssafy.board.data.toReorderCardData
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationLarge
import com.ssafy.designsystem.values.PaddingDefault
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
        onCardReordered = viewModel::updateCardOrder,
        onListReordered = viewModel::updateListOrder,
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
    onListReordered: () -> Unit,
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
            onListReordered = onListReordered,
        )
    }
}

@Composable
private fun BoardScreenBody

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
        popBack = {}
    )
}