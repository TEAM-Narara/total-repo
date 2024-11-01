package com.ssafy.card.label.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.card.label.data.LabelData
import com.ssafy.designsystem.component.EditText
import com.ssafy.designsystem.dialog.BaseDialog
import com.ssafy.designsystem.dialog.DialogState
import com.ssafy.designsystem.dialog.rememberDialogState
import com.ssafy.designsystem.getContrastingTextColor
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingTwo
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.backgroundColorList

@Composable
fun CreateLabelDialog(
    modifier: Modifier = Modifier,
    dialogState: DialogState<LabelData>,
    onConfirm: (Long, String) -> Unit,
) {
    var color by remember { mutableStateOf(dialogState.parameter?.color) }
    var description by remember { mutableStateOf(dialogState.parameter?.description ?: "") }

    LaunchedEffect(dialogState.isVisible) {
        color = dialogState.parameter?.color
        description = dialogState.parameter?.description ?: ""
    }

    BaseDialog(
        modifier = modifier,
        dialogState = dialogState,
        title = "라벨 생성",
        confirmText = "생성",
        onConfirm = { onConfirm(color!!, description) },
        validation = { color != null },
    ) {
        Column {
            Text(
                text = "색상",
                fontSize = TextSmall,
                color = Primary
            )
            Spacer(modifier = Modifier.height(PaddingSmall))
            LazyVerticalGrid(
                columns = GridCells.Fixed(5), modifier = Modifier.fillMaxWidth(),
            ) {
                items(backgroundColorList) { labelColor ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(PaddingTwo, PaddingTwo)
                            .background(Color(labelColor), shape = RoundedCornerShape(PaddingSmall))
                            .clickable { color = labelColor }
                            .then(
                                if (color == labelColor) Modifier.border(
                                    width = 2.dp,
                                    color = getContrastingTextColor(Color(labelColor)),
                                    shape = RoundedCornerShape(PaddingSmall)
                                ) else Modifier
                            ),
                    ) {
                        if (color == labelColor) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = getContrastingTextColor(Color(labelColor))
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(PaddingDefault))
            EditText(
                title = "설명",
                text = description,
                onTextChange = { description = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun CreateLabelDialogPreview() {
    val dialogState = rememberDialogState<LabelData>()
    CreateLabelDialog(
        dialogState = dialogState.apply { show() },
        onConfirm = { _, _ -> },
    )
}