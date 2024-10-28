package com.ssafy.board.boardMenu

import android.app.Activity
import android.graphics.Color.parseColor
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.ssafy.board.components.BoardMemberItem
import com.ssafy.board.components.MenuEditTextRow
import com.ssafy.board.components.MenuHorizontalDivider
import com.ssafy.board.data.HistoryData
import com.ssafy.board.getIcon
import com.ssafy.designsystem.component.ActivityLog
import com.ssafy.designsystem.values.ImageSmall
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextXLarge
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardMenuScreen(
    modifier: Modifier = Modifier,
    boardId: Long,
    workspaceId: Long,
    backHome: () -> Unit,
    historyContent: List<HistoryData>?,
) {

    val (boardName, onBoardNameChange) = remember { mutableStateOf("board 이름") }
    val (workspaceName, onWorkspaceNameChange) = remember { mutableStateOf("손오공's 워크스페이스") }
    val (background, onBackgroundChange) = remember { mutableStateOf("#FFF7BD") }
    val (watch, onWatchChange) = remember { mutableStateOf(true) }
    val (visibility, onVisibilityChange) = remember { mutableStateOf("WORKSPACE") }
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
            visibility = visibility,
            onVisibilityChange
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
                    modifier
                        .padding(PaddingDefault)
                        .padding(bottom = PaddingZero)
                )
                // TODO: Memeber도 넘기기
            }
            item {
                MenuHorizontalDivider()
            }
            item {
                MenuEditTextRow(modifier, "Name", boardName, onBoardNameChange)
            }
            item {
                MenuHorizontalDivider()
            }
            item {
                MenuEditTextRow(modifier, "WorkSpace", workspaceName, onWorkspaceNameChange)
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
                            .background(color = Color(parseColor(background)))
                    )
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
                        checked = watch,
                        onCheckedChange = onWatchChange,
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
                    modifier = Modifier
                        .padding(PaddingDefault, PaddingZero)
                ) {
                    Text(text = "Visibility", fontSize = TextMedium, color = Primary)
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = visibility, modifier = Modifier.clickable(onClick = {
                        setShowDialog(true)
                    }))
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
            items(historyContent?.size ?: 0) { index ->
                historyContent?.let {
                    ActivityLog(
                        icon = getIcon(historyContent[index].type),
                        content = historyContent[index].content,
                        editDate = historyContent[index].date,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    BoardMenuScreen(
        boardId = 1,
        backHome = {},
        workspaceId = 1,
        historyContent = List(8) { HistoryData("rename", "손오공 renamed test(from testboard)", 300) }
    )
}
