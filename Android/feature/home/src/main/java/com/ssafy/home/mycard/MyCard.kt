package com.ssafy.home.mycard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.designsystem.values.Yellow
import com.ssafy.designsystem.values.toColor
import com.ssafy.model.with.BoardInMyRepresentativeCard
import com.ssafy.model.with.CoverType
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun MyCardScreen(
    viewModel: MyCardViewModel = hiltViewModel(),
    popBackToHome: () -> Unit,
    moveToCardScreen: (boardId: Long, cardId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val myCardList by viewModel.myCardList.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.resetUiState() }

    MyCardScreen(
        boardList = myCardList,
        popBackToHome = popBackToHome,
        moveToCardScreen = moveToCardScreen
    )

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}

@Composable
private fun MyCardScreen(
    boardList: List<BoardInMyRepresentativeCard>,
    popBackToHome: () -> Unit,
    moveToCardScreen: (boardId: Long, cardId: Long) -> Unit
) {
    Scaffold(
        topBar = { MyCardTopBar(onNavigateClick = popBackToHome) }
    ) { innerPadding ->

        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(boardList.size) { index ->
                val board: BoardInMyRepresentativeCard = boardList[index]
                val coverType = runCatching { CoverType.valueOf(board.coverType ?: "") }
                    .getOrDefault(CoverType.NONE)

                BoardWithMyCards(
                    board = board,
                    boardIcon = {
                        when (coverType) {
                            CoverType.COLOR -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color = board.coverValue?.toColor() ?: Yellow)
                                )
                            }

                            CoverType.IMAGE -> {
                                AsyncImage(
                                    model = board.coverValue,
                                    contentDescription = "Board Cover",
                                    contentScale = ContentScale.Crop,
                                    error = ColorPainter(Yellow)
                                )
                            }
                            else -> {}
                        }
                    },
                    onClick = { cardId -> moveToCardScreen(board.id, cardId) }
                )
            }
        }
    }
}

@Composable
@Preview
private fun MyCardScreenPreview() {
    MyCardScreen(
        popBackToHome = {},
        moveToCardScreen = { _, _ -> }
    )
}
