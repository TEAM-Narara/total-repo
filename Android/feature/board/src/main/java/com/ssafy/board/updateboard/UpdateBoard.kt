package com.ssafy.board.updateboard

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.board.updateboard.data.BoardData
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.Primary
import com.ssafy.model.background.Cover

@Composable
fun UpdateBoardScreen(
    viewModel: UpdateBoardViewModel = hiltViewModel(),
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Cover?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boardData by viewModel.boardData.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            UpdateBoardTopBar(onNavigateClick = popBackToHome)
        }
    ) { innerPadding ->
        boardData?.let {
            UpdateBoardScreen(
                modifier = Modifier.padding(innerPadding),
                boardData = it,
                moveToSelectBackgroundScreen = moveToSelectBackgroundScreen
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
private fun UpdateBoardScreen(
    modifier: Modifier = Modifier,
    boardData: BoardData,
    moveToSelectBackgroundScreen: (Cover?) -> Unit
) {
    with(LocalContext.current as Activity) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            window.statusBarColor = Primary.toArgb()
        }
    }

    UpdateBoardBody(
        modifier = modifier.fillMaxSize(),
        boardTitle = boardData.title,
        workSpaceTitle = boardData.workspaceTitle,
        cover = null,
        moveToSelectBackgroundScreen = moveToSelectBackgroundScreen,
        updateBoardClick = { /*TODO 보드 생성 로직 생기면 추가*/ }
    )
}

@Composable
@Preview
private fun UpdateBoardPreview() {
    UpdateBoardScreen(
        boardData = BoardData(
            id = 0L,
            title = "board",
            workspaceTitle = "workspace",
            background = Unit,
            visibility = "Workspace",
        ),
        moveToSelectBackgroundScreen = {}
    )
}