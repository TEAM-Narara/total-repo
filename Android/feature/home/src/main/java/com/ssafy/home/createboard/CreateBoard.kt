package com.ssafy.home.createboard

import android.app.Activity
import android.util.Log
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
import com.ssafy.model.background.Cover
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.with.CoverType
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun CreateBoardScreen(
    viewModel: CreateBoardViewModel = hiltViewModel(),
    cover: Cover,
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Cover) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val workspaceList by viewModel.workspace.collectAsStateWithLifecycle()

    Log.d("TAG", "CreateBoardScreen: $workspaceList")

    LaunchedEffect(Unit) {
        viewModel.resetUiState()
        viewModel.getWorkspaceList()
        viewModel.changeCover(cover)
    }

    CreateBoardScreen(
        boardData = viewModel.boardData,
        workspaceList = workspaceList,
        popBackToHome = popBackToHome,
        moveToSelectBackgroundScreen = moveToSelectBackgroundScreen,
        createBoard = { viewModel.createBoard(popBackToHome) },
        changeBoardName = viewModel::changeBoardName,
        changeWorkspace = viewModel::changeWorkspaceId,
        changeVisibleScope = viewModel::changeVisibility,
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
    boardData: BoardDTO,
    workspaceList: List<WorkSpaceDTO>,
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Cover) -> Unit,
    createBoard: () -> Unit,
    changeBoardName: (String) -> Unit,
    changeWorkspace: (WorkSpaceDTO) -> Unit,
    changeVisibleScope: (Visibility) -> Unit,
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
            boardData = boardData,
            workSpaceList = workspaceList,
            moveToSelectBackgroundScreen = moveToSelectBackgroundScreen,
            createBoardClick = createBoard,
            changeBoardName = changeBoardName,
            changeWorkspace = changeWorkspace,
            changeVisibleScope = changeVisibleScope
        )
    }
}

@Composable
@Preview
private fun CreateBoardPreview() {
    CreateBoardScreen(
        cover = Cover(
            type = CoverType.COLOR,
            value = "#FFFFFF"
        ),
        popBackToHome = {},
        moveToSelectBackgroundScreen = {}
    )
}
