package com.ssafy.card.label.dialogs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.dialog.BaseDialog
import com.ssafy.designsystem.dialog.DialogState
import com.ssafy.designsystem.dialog.rememberDialogState

@Composable
fun DeleteLabelDialog(
    modifier: Modifier = Modifier,
    dialogState: DialogState<Long>,
    onConfirm: (Long) -> Unit,
) {
    BaseDialog(
        modifier = modifier,
        dialogState = dialogState,
        title = "라벨 삭제",
        confirmText = "삭제",
        onConfirm = { dialogState.parameter?.let { onConfirm(it) } },
        validation = { dialogState.parameter != null },
    ) {
        Text(text = "삭제하시겠습니까?")
    }
}

@Preview
@Composable
private fun DeleteLabelDialogPreview() {
    val dialogState = rememberDialogState<Long>()
    DeleteLabelDialog(
        dialogState = dialogState.apply { show() },
        onConfirm = {},
    )
}