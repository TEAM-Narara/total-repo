package com.ssafy.home.mycard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.designsystem.values.Primary

@Composable
fun MyCardScreen(
    viewModel: MyCardViewModel = hiltViewModel(),
    popUpToHome: () -> Unit,
    moveToCardScreen: (Any) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyCardScreen(
        // TODO : BoardList의 타입이 지정되면 수정
        boardList = uiState.boards,
        popUpToHome = popUpToHome,
        // TODO : Card에 대한 타입이 지정되면 수정
        moveToCardScreen = moveToCardScreen
    )
}

@Composable
private fun MyCardScreen(
    boardList: List<Any>,
    popUpToHome: () -> Unit,
    moveToCardScreen: (Any) -> Unit
) {
    Scaffold(
        topBar = { MyCardTopBar(onNavigateClick = popUpToHome) }
    ) { innerPadding ->

        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(boardList.size) { board ->
                BoardWithMyCards(
                    board = board,
                    // TODO : BoardIcon에 대한 이미지가 있으면 수정
                    boardIcon = {
                        Image(
                            painter = ColorPainter(Primary),
                            contentDescription = null
                        )
                    },
                    onClick = moveToCardScreen
                )
            }
        }
    }
}

@Composable
@Preview
private fun MyCardScreenPreview() {
    MyCardScreen(
        popUpToHome = {},
        moveToCardScreen = {}
    )
}
