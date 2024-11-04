package com.ssafy.home.home

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.designsystem.values.White
import com.ssafy.home.drawer.DrawerSheet
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: (List<String>) -> Unit,
    moveToLoginScreen: () -> Unit,
    moveToSettingScreen: () -> Unit,
    moveToMyCardScreen: () -> Unit,
    moveToUpdateProfile: () -> Unit,
    moveToSearchScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity
    activity?.let {
        WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
            isAppearanceLightStatusBars = true
            it.window.statusBarColor = Color.White.toArgb()
        }
    }

    LaunchedEffect(Unit) { viewModel.resetUiState() }

    HomeScreen(
        workSpace = Any(),
        moveToBoardScreen = moveToBoardScreen,
        moveToCreateNewBoardScreen = moveToCreateNewBoardScreen,
        moveToCreateNewWorkSpaceScreen = { /*TODO 새 워크 스페이스 만들기 */ },
        moveToLoginScreen = { viewModel.logout(moveToLoginScreen) },
        moveToSettingScreen = moveToSettingScreen,
        moveToMyCardScreen = moveToMyCardScreen,
        moveToUpdateProfile = moveToUpdateProfile,
        moveToSearchScreen = moveToSearchScreen,
    )

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}

@Composable
private fun HomeScreen(
    workSpace: Any?,
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: (List<String>) -> Unit,
    moveToCreateNewWorkSpaceScreen: () -> Unit,
    moveToLoginScreen: () -> Unit,
    moveToSettingScreen: () -> Unit,
    moveToMyCardScreen: () -> Unit,
    moveToUpdateProfile: () -> Unit,
    moveToSearchScreen: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val spanCount = if (isPortrait) 2 else 4
    val scope = rememberCoroutineScope()

    val url =
        "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z"

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                icon = {
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                },
                nickname = "손오공",
                email = "monkey@naver.com",
                workspaceList = List(4) { "손오공's workspace" },
                onAddWorkSpaceClick = { /*TODO*/ },
                onMyBoardClick = { /*TODO*/ },
                onMyCardClick = moveToMyCardScreen,
                onSettingClick = moveToUpdateProfile,
                onLogoutClick = moveToLoginScreen,
                onWorkSpaceClick = { /*TODO*/ }
            )
        }
    ) {
        Scaffold(
            containerColor = White,
            topBar = {
                MainTopBar(
                    onDrawerClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { moveToSearchScreen() },
                    onAlarmClick = { /*TODO*/ },
                    onMenuClick = moveToSettingScreen
                )
            },
            floatingActionButton = {
                if (workSpace != null) {
                    AddNewBoardFloatingButton(
                        moveToCreateNewBoardScreen = {
                            moveToCreateNewBoardScreen(
                                // TODO : WorkSpaceList에 대한 DTO 변경 필요
                                List(4) { "workspace-$it" }
                            )
                        }
                    )
                }
            }
        ) { innerPadding ->
            if (workSpace != null) {
                HomeBodyScreen(
                    modifier = Modifier.padding(innerPadding),
                    // TODO : Board에 대한 정보를 전달합니다.
                    boards = List(4) { Any() },
                    spanCount = spanCount,
                    moveToBoardScreen = moveToBoardScreen
                )
            } else {
                HomeEmptyScreen(
                    modifier = Modifier.padding(innerPadding),
                    moveToCreateNewWorkSpaceScreen = moveToCreateNewWorkSpaceScreen
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    HomeScreen(
        workSpace = Any(),
        moveToBoardScreen = {},
        moveToCreateNewBoardScreen = {},
        moveToCreateNewWorkSpaceScreen = {},
        moveToLoginScreen = {},
        moveToSettingScreen = {},
        moveToMyCardScreen = {},
        moveToUpdateProfile = {},
        moveToSearchScreen = {}
    )
}
