package com.ssafy.designsystem.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ssafy.designsystem.values.CornerLarge
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.ReversePrimary
import com.ssafy.designsystem.values.White

@Composable
fun <T> BaseDialog(
    modifier: Modifier = Modifier,
    dialogState: DialogState<T>,
    title: String?,
    confirmText: String = "확인",
    dismissText: String = "취소",
    content: (@Composable () -> Unit)?,
) {
    if (dialogState.isVisible) AlertDialog(
        modifier = modifier,
        onDismissRequest = dialogState::dismiss,
        shape = RoundedCornerShape(CornerLarge),
        title = title?.let { { Text(text = it) } },
        containerColor = White,
        text = content,
        confirmButton = {
            TextButton(onClick = dialogState::confirm) {
                Text(confirmText, color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = dialogState::dismiss) {
                Text(dismissText, color = ReversePrimary)
            }
        }
    )
}