package com.ssafy.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.designsystem.values.BoxDefault
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.TextXXLarge
import com.ssafy.designsystem.values.White

@Composable
fun DrawerSheet(
    modifier: Modifier = Modifier,
    nickname: String,
    email: String,
    // TODO : workspaceList를 받아와서 보여주기
    workspaceList: List<String>,
    icon: @Composable () -> Unit,
    onWorkSpaceClick: (String) -> Unit,
    onAddWorkSpaceClick: () -> Unit,
    onMyBoardClick: () -> Unit,
    onMyCardClick: () -> Unit,
    onSettingClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    ModalDrawerSheet(
        modifier = modifier.fillMaxWidth(0.8f),
        drawerContainerColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            DrawerSheetHead(
                nickname = nickname,
                email = email,
                icon = icon,
                onLogoutClick = onLogoutClick
            )

            SheetItem(
                modifier = Modifier.background(color = Gray),
                sheetIcon = Icons.Default.Inbox,
                sheetName = "Boards",
                onSheetItemClick = { onMyBoardClick() }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BoxDefault)
                    .padding(horizontal = PaddingDefault)
            ) {
                Text(
                    text = "Workspaces",
                    fontSize = TextXXLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                IconButton(
                    onClick = { onAddWorkSpaceClick() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .then(Modifier.size(IconLarge)),
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Workspace 추가")
                }
            }

            workspaceList.forEach { workspace ->
                SheetItem(
                    sheetIcon = Icons.Default.Groups,
                    sheetName = workspace,
                    onSheetItemClick = { onWorkSpaceClick(workspace) }
                )
            }

            SheetItem(
                sheetIcon = Icons.Outlined.EmojiEmotions,
                sheetName = "My Cards",
                onSheetItemClick = { onMyCardClick() }
            )

            SheetItem(
                sheetIcon = Icons.Default.PersonOutline,
                sheetName = "회원 정보 수정",
                onSheetItemClick = { onSettingClick() })
        }
    }
}