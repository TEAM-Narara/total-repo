package com.ssafy.home.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.designsystem.values.White
import com.ssafy.home.drawer.DrawerSheet
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        workSpace = uiState.workSpace,
        moveToBoardScreen = moveToBoardScreen,
        moveToCreateNewBoardScreen = moveToCreateNewBoardScreen,
        moveToCreateNewWorkSpaceScreen = { /*TODO 새 워크 스페이스 만들기 */ }
    )
}

@Composable
private fun HomeScreen(
    workSpace: Any?,
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: () -> Unit,
    moveToCreateNewWorkSpaceScreen: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val spanCount = if (isPortrait) 2 else 4
    val scope = rememberCoroutineScope()

    val url =
        "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                icon = { AsyncImage(model = url, contentDescription = null, contentScale= ContentScale.Crop) },
                nickname = "손오공",
                email = "monkey@naver.com",
                workspaceList = List(4) { "손오공's workspace" },
                onAddWorkSpaceClick = { /*TODO*/ },
                onMyBoardClick = { /*TODO*/ },
                onMyCardClick = { /*TODO*/ },
                onSettingClick = { /*TODO*/ },
                onLogoutClick = { /*TODO*/ },
                onWorkSpaceClick = { /*TODO*/ }
            )
        }
    ) {
        Scaffold(
            containerColor = White,
            topBar = {
                MainTopBar(
                    onDrawerClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { /*TODO*/ },
                    onAlarmClick = { /*TODO*/ },
                    onMenuClick = { /*TODO*/ }
                )
            },
            floatingActionButton = {
                if (workSpace != null) {
                    AddNewBoardFloatingButton(
                        moveToCreateNewBoardScreen = moveToCreateNewBoardScreen
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
        moveToCreateNewWorkSpaceScreen = {}
    )
}
