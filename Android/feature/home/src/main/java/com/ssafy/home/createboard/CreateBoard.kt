package com.ssafy.home.createboard

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.designsystem.values.Primary
import com.ssafy.model.background.Background
import com.ssafy.model.background.BackgroundType
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun CreateBoardScreen(
    viewModel: CreateBoardViewModel = hiltViewModel(),
    background: Background,
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Background?) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.resetUiState() }

    CreateBoardScreen(
        background = background,
        workspaceList = emptyList(),
        popBackToHome = popBackToHome,
        moveToSelectBackgroundScreen = moveToSelectBackgroundScreen,
        createBoard = viewModel::createBoard,
    )

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}

@Composable
private fun CreateBoardScreen(
    background: Background,
    workspaceList: List<WorkSpaceDTO>,
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Background?) -> Unit,
    createBoard: (BoardDTO) -> Unit,
) {
    with(LocalContext.current as Activity) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            window.statusBarColor = Primary.toArgb()
        }
    }

    Scaffold(
        topBar = {
            CreateBoardTopBar(onNavigateClick = popBackToHome)
        }
    ) { innerPadding ->

        CreateBoardBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            background = background,
            workSpaceList = workspaceList,
            moveToSelectBackgroundScreen = moveToSelectBackgroundScreen,
            createBoardClick = createBoard,
        )
    }
}

@Composable
@Preview
private fun CreateBoardPreview() {
    CreateBoardScreen(
        background = Background(
            type = BackgroundType.COLOR,
            value = "#FFFFFF"
        ),
        popBackToHome = {},
        moveToSelectBackgroundScreen = {}
    )
}
