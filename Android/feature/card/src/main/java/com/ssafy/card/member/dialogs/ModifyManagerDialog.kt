package com.ssafy.card.member.dialogs

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.card.member.components.ManagerItem
import com.ssafy.card.member.data.ManagerData
import com.ssafy.designsystem.dialog.BaseDialog
import com.ssafy.designsystem.dialog.DialogState
import com.ssafy.designsystem.dialog.rememberDialogState

@Composable
fun ModifyManagerDialog(
    modifier: Modifier = Modifier,
    dialogState: DialogState<Unit>,
    memberList: List<ManagerData>,
    onIsManagerChanged: (Long, Boolean) -> Unit
) {
    BaseDialog(
        modifier = modifier,
        dialogState = dialogState,
        title = "담당자",
        dismissText = null
    ) {
        val managerList by remember { mutableStateOf(memberList) }

        LazyColumn {
            items(managerList) {
                ManagerItem(
                    managerData = it,
                    onIsManagerChanged = onIsManagerChanged
                )
            }
        }
    }
}

@Preview
@Composable
private fun ModifyManagerDialogPrev() {
    ModifyManagerDialog(
        dialogState = rememberDialogState<Unit>().apply { show() },
        memberList = (0..10L).map {
            ManagerData(
                id = it,
                nickname = "nickname",
                email = "email@example.com",
                isManager = true
            )
        },
        onIsManagerChanged = { _, _ -> }
    )
}