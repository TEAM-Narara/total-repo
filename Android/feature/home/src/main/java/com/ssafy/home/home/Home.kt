package com.ssafy.home.home

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.designsystem.dialog.rememberDialogState
import com.ssafy.designsystem.values.White
import com.ssafy.home.data.SelectedWorkSpace
import com.ssafy.home.drawer.DrawerSheet
import com.ssafy.model.user.User
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    moveToBoardScreen: (Long, Long) -> Unit,
    moveToCreateNewBoardScreen: (List<WorkSpaceDTO>) -> Unit,
    moveToLoginScreen: () -> Unit,
    moveToSettingScreen: (Long) -> Unit,
    moveToMyCardScreen: () -> Unit,
    moveToUpdateProfile: () -> Unit,
    moveToSearchScreen: () -> Unit,
    moveToAlarmScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val homeData by viewModel.homeData.collectAsStateWithLifecycle(null)
    val activity = LocalContext.current as? Activity
    activity?.let {
        WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
            isAppearanceLightStatusBars = true
            it.window.statusBarColor = Color.White.toArgb()
        }
    }

    val createDialogState = rememberDialogState<Unit>()

    LaunchedEffect(Unit) { viewModel.resetUiState() }

    homeData?.let { data ->
        HomeScreen(
            user = data.user,
            workSpaceList = data.workspaceList,
            selectedWorkspace = data.selectedWorkSpace,
            moveToBoardScreen = { boardId ->
                viewModel.homeData.value?.selectedWorkSpace?.workspaceId?.let { workspaceId ->
                    moveToBoardScreen(workspaceId, boardId)
                }
            },
            moveToCreateNewBoardScreen = { moveToCreateNewBoardScreen(data.workspaceList) },
            moveToLoginScreen = { viewModel.logout(moveToLoginScreen) },
            moveToSettingScreen = { moveToSettingScreen(data.selectedWorkSpace.workspaceId) },
            moveToMyCardScreen = moveToMyCardScreen,
            moveToUpdateProfile = moveToUpdateProfile,
            moveToSearchScreen = moveToSearchScreen,
            moveToAlarmScreen = moveToAlarmScreen,
            moveToJoinedBoard = { /*TODO 가입한 보드 화면으로 이동 */ },
            addNewWorkSpace = { createDialogState.show() },
            chaneSelectedWorkSpace = viewModel::updateSelectedWorkspace
        )
    } ?: LoadingScreen()

    CreateWorkspaceDialog(
        dialogState = createDialogState,
        onConfirm = viewModel::createWorkspace
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
    user: User,
    workSpaceList: List<WorkSpaceDTO>,
    selectedWorkspace: SelectedWorkSpace,
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: () -> Unit,
    moveToLoginScreen: () -> Unit,
    moveToSettingScreen: () -> Unit,
    moveToMyCardScreen: () -> Unit,
    moveToUpdateProfile: () -> Unit,
    moveToSearchScreen: () -> Unit,
    moveToAlarmScreen: () -> Unit,
    moveToJoinedBoard: () -> Unit,
    addNewWorkSpace: () -> Unit,
    chaneSelectedWorkSpace: (Long) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val spanCount = if (isPortrait) 2 else 4
    val scope = rememberCoroutineScope()

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
                        modifier = Modifier.fillMaxSize(),
                        model = user.profileImgUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(Icons.Default.AccountCircle),
                    )
                },
                nickname = user.nickname,
                email = user.email,
                workspaceList = workSpaceList,
                onAddWorkSpaceClick = addNewWorkSpace,
                moveToJoinedBoard = moveToJoinedBoard,
                onMyCardClick = moveToMyCardScreen,
                onSettingClick = moveToUpdateProfile,
                onLogoutClick = moveToLoginScreen,
                onWorkSpaceClick = { chaneSelectedWorkSpace(it).also { scope.launch { drawerState.close() } } }
            )
        }
    ) {
        Scaffold(
            containerColor = White,
            topBar = {
                MainTopBar(
                    title = selectedWorkspace.workspaceName,
                    onDrawerClick = { scope.launch { drawerState.open() } },
                    onSearchClick = moveToSearchScreen,
                    onAlarmClick = moveToAlarmScreen,
                    onMenuClick = moveToSettingScreen
                )
            },
            floatingActionButton = {
                if (workSpaceList.isNotEmpty()) {
                    AddNewBoardFloatingButton(
                        moveToCreateNewBoardScreen = moveToCreateNewBoardScreen
                    )
                }
            }
        ) { innerPadding ->
            if (workSpaceList.isNotEmpty()) {
                HomeBodyScreen(
                    modifier = Modifier.padding(innerPadding),
                    boards = selectedWorkspace.boards,
                    spanCount = spanCount,
                    moveToBoardScreen = moveToBoardScreen
                )
            } else {
                HomeEmptyScreen(
                    modifier = Modifier.padding(innerPadding),
                    addNewWorkSpace = addNewWorkSpace
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    HomeScreen(
        workSpaceList = emptyList(),
        selectedWorkspace = SelectedWorkSpace(),
        user = User(0L, "", "", null),
        moveToBoardScreen = {},
        moveToCreateNewBoardScreen = {},
        moveToLoginScreen = {},
        moveToSettingScreen = {},
        moveToMyCardScreen = {},
        moveToUpdateProfile = {},
        moveToSearchScreen = {},
        moveToAlarmScreen = {},
        moveToJoinedBoard = {},
        addNewWorkSpace = {},
        chaneSelectedWorkSpace = {}
    )
}
