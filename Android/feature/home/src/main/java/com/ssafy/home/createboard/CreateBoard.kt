package com.ssafy.home.createboard

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.designsystem.values.Primary

@Composable
fun CreateBoardScreen(
    viewModel: CreateBoardViewModel = hiltViewModel(),
    workspaceList: List<String>,
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateBoardScreen(
        board = uiState.board,
        workSpaceList = workspaceList,
        background = uiState.background,
        popBackToHome = popBackToHome,
        moveToSelectBackgroundScreen = moveToSelectBackgroundScreen
    )
}

@Composable
private fun CreateBoardScreen(
    // UI STATE의 board, workSpaceList, background를 받아옴
    board: Any,
    workSpaceList: List<Any>,
    background: Any,
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: () -> Unit
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
            board = board,
            workSpaceList = workSpaceList,
            background = background,
            moveToSelectBackgroundScreen = moveToSelectBackgroundScreen,
            createBoardClick = { /*TODO 보드 생성 로직 생기면 추가*/ }
        )
    }
}

@Composable
@Preview
private fun CreateBoardPreview() {
    CreateBoardScreen(
        board = Any(),
        workSpaceList = List(4) { "테스트-$it" },
        background = Any(),
        popBackToHome = {},
        moveToSelectBackgroundScreen = {}
    )
}