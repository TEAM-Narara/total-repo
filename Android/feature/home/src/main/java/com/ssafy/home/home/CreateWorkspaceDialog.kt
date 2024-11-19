package com.ssafy.home.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ssafy.designsystem.dialog.BaseDialog
import com.ssafy.designsystem.dialog.DialogState

@Composable
fun CreateWorkspaceDialog(
    modifier: Modifier = Modifier,
    dialogState: DialogState<Unit>,
    onConfirm: () -> Unit,
) {
    BaseDialog(
        modifier = modifier,
        dialogState = dialogState,
        title = "Workspace 생성",
        onConfirm = onConfirm,
        confirmText = "생성",
        dismissText = "취소",
    ) {
        Text(text = "Workspace를 생성하시겠습니까?")
    }
}