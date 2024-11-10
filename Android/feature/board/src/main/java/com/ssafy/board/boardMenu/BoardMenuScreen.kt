package com.ssafy.board.boardMenu

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.board.board.components.BoardMemberItem
import com.ssafy.board.boardMenu.data.BoardMenuData
import com.ssafy.board.components.MenuEditTextRow
import com.ssafy.board.components.MenuHorizontalDivider
import com.ssafy.designsystem.values.ImageSmall
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingOne
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextXLarge
import com.ssafy.designsystem.values.White
import com.ssafy.designsystem.values.toColor
import com.ssafy.model.background.Cover
import com.ssafy.model.board.Visibility
import com.ssafy.model.with.CoverType
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun BoardMenuScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardMenuViewModel = hiltViewModel(),
    popBack: () -> Unit,
    cover: Cover,
    moveToSelectBackGroundScreen: (Cover) -> Unit,
    moveToInviteMemberScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boardState by viewModel.boardState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.resetUiState() }

    boardState?.let { boardMenuData ->
        BoardMenuScreen(
            modifier = modifier,
            boardMenuData = boardMenuData,
            backHome = popBack,
            moveToSelectBackGroundScreen = moveToSelectBackGroundScreen,
            moveToInviteMemberScreen = moveToInviteMemberScreen,
            cover = cover,
            changeBoardName = viewModel::changeBoardName,
            changeWorkspaceName = viewModel::changeWorkspaceName,
            changeWatch = viewModel::changeWatch,
            changeVisibility = viewModel::changeVisibility,
            deleteBoard = {
                viewModel.deleteBoard {
                    popBack()
                    popBack()
                }
            }
        )
    } ?: LoadingScreen()

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let {
            ErrorScreen(
                errorMessage = it,
                afterAction = popBack
            )
        }

        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoardMenuScreen(
    modifier: Modifier = Modifier,
    boardMenuData: BoardMenuData,
    backHome: () -> Unit,
    moveToSelectBackGroundScreen: (Cover) -> Unit,
    moveToInviteMemberScreen: () -> Unit,
    cover: Cover,
    changeBoardName: (String) -> Unit,
    changeWorkspaceName: (String) -> Unit,
    changeWatch: (Boolean) -> Unit,
    changeVisibility: (Visibility) -> Unit,
    deleteBoard: () -> Unit
) {
    val activity = LocalContext.current as? Activity
    activity?.let {
        WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
            isAppearanceLightStatusBars = false
            it.window.statusBarColor = Primary.toArgb()
        }
    }
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    if (showDialog) {
        VisibilityDialog(
            onDismiss = { setShowDialog(false) },
            visibility = boardMenuData.boardDto.visibility,
            setVisibility = changeVisibility
        )
    }
    Scaffold(
        containerColor = White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
                navigationIcon = {
                    IconButton(onClick = { backHome() }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "취소",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(text = "Board Menu", fontSize = TextXLarge, color = Color.White)
                },
                actions = {
                    IconButton(onClick = deleteBoard) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "삭제",
                            tint = White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(PaddingDefault)
        ) {
            item {
                BoardMemberItem(
                    modifier = modifier
                        .padding(PaddingDefault)
                        .padding(bottom = PaddingZero),
                    boardMember = boardMenuData.members,
                    moveToBoardInviteMemberScreen = moveToInviteMemberScreen
                )
            }
            item {
                MenuHorizontalDivider()
            }
            item {
                val name = boardMenuData.boardDto.name
                MenuEditTextRow(modifier, "Name", name, changeBoardName)
            }
            item {
                MenuHorizontalDivider()
            }
            item {
                val workspaceName = boardMenuData.workSpaceDTO.name
                MenuEditTextRow(modifier, "WorkSpace", workspaceName, changeWorkspaceName)
            }
            item {
                MenuHorizontalDivider()
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PaddingXSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(PaddingDefault, PaddingZero)
                ) {
                    Text(text = "Background", fontSize = TextMedium, color = Primary)
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(ImageSmall)
                            .clickable { moveToSelectBackGroundScreen(cover) }) {
                        when (cover.type) {
                            CoverType.COLOR -> {
                                Box(
                                    modifier = Modifier
                                        .size(ImageSmall)
                                        .background(color = cover.value.toColor())
                                        .shadow(PaddingOne, spotColor = Color.LightGray)
                                )
                            }

                            CoverType.IMAGE -> {
                                AsyncImage(
                                    modifier = Modifier.size(ImageSmall),
                                    model = cover.value,
                                    contentDescription = null
                                )
                            }

                            CoverType.NONE -> {
                                Box(
                                    modifier = Modifier
                                        .size(ImageSmall)
                                        .background(color = Color.LightGray)
                                        .shadow(PaddingOne, spotColor = Color.LightGray)
                                )
                            }
                        }
                    }
                }
            }
            item {
                MenuHorizontalDivider()
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(PaddingDefault, PaddingZero)
                ) {
                    Text(text = "Watch", fontSize = TextMedium, color = Primary)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = boardMenuData.watchStatus,
                        onCheckedChange = changeWatch,
                        colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                    )
                }
            }
            item {
                MenuHorizontalDivider()
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PaddingXSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(PaddingDefault, PaddingZero)
                ) {
                    Text(text = "Visibility", fontSize = TextMedium, color = Primary)
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = boardMenuData.boardDto.visibility.name,
                        modifier = Modifier.clickable(onClick = { setShowDialog(true) })
                    )
                }
            }
            item {
                MenuHorizontalDivider()
            }
            item {
                Text(
                    text = "History",
                    fontSize = TextMedium,
                    color = Primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingDefault, PaddingZero)
                )
            }
            // TODO ActivityLog 라는 screen이 있습니다. 이것으로 historyContent를 만들어주세요
        }
    }


}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    BoardMenuScreen(
        popBack = {},
        moveToSelectBackGroundScreen = {},
        moveToInviteMemberScreen = {},
        cover = Cover(
            type = CoverType.COLOR,
            value = "#FFFFFF"
        )
    )
}
