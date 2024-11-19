package com.ssafy.card.label

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.card.label.components.EditableLabelItem
import com.ssafy.card.label.components.LabelTopAppBar
import com.ssafy.card.label.data.LabelData
import com.ssafy.card.label.dialogs.CreateLabelDialog
import com.ssafy.card.label.dialogs.DeleteLabelDialog
import com.ssafy.card.label.dialogs.ModifyLabelDialog
import com.ssafy.designsystem.dialog.rememberDialogState
import com.ssafy.designsystem.values.BorderDefault
import com.ssafy.designsystem.values.CornerSmall
import com.ssafy.designsystem.values.LightGray
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall

@Composable
fun LabelScreen(
    modifier: Modifier = Modifier,
    viewModel: LabelViewModel = hiltViewModel(),
    popBack: () -> Unit,
) {
    val labelList by viewModel.labelList.collectAsStateWithLifecycle()

    val createDialogState = rememberDialogState<LabelData>()
    val modifyDialogState = rememberDialogState<LabelData>()
    val deleteDialogState = rememberDialogState<Long>()

    Scaffold(
        modifier = modifier,
        topBar = { LabelTopAppBar(onClosePressed = popBack) },
    ) { paddingValues ->
        labelList?.let {
            LabelScreen(
                modifier = Modifier.padding(paddingValues),
                labelList = it,
                onLabelSelect = viewModel::selectLabel,
                onCreateLabel = createDialogState::show,
                onEditLabel = modifyDialogState::show,
                onDeleteLabel = deleteDialogState::show,
            )
        }
    }

    CreateLabelDialog(dialogState = createDialogState, onConfirm = viewModel::createLabel)
    ModifyLabelDialog(dialogState = modifyDialogState, onConfirm = viewModel::updateLabel)
    DeleteLabelDialog(dialogState = deleteDialogState, onConfirm = viewModel::deleteLabel)
}

@Composable
fun LabelScreen(
    modifier: Modifier = Modifier,
    labelList: List<LabelData>,
    onLabelSelect: (Long, Boolean) -> Unit,
    onCreateLabel: () -> Unit,
    onEditLabel: (LabelData) -> Unit,
    onDeleteLabel: (Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = PaddingDefault),
        verticalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {
        items(labelList) {
            EditableLabelItem(
                labelData = it,
                onLabelSelect = onLabelSelect,
                onEditLabel = onEditLabel,
                onDeleteLabel = onDeleteLabel
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        color = LightGray,
                        width = BorderDefault,
                        shape = RoundedCornerShape(CornerSmall),
                    )
                    .clickable { onCreateLabel() }
                    .padding(PaddingSmall),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "isSelected",
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Preview
@Composable
private fun LabelScreenPreview() {
    LabelScreen(
        labelList = listOf(
            LabelData(
                id = 0L,
                color = Color(0xFF4BCE97),
                description = "",
                isSelected = true
            ),
            LabelData(
                id = 1L,
                color = Color(0xFFF5CD47),
                description = "",
                isSelected = false
            ),
            LabelData(
                id = 0L,
                color = Color(0xFFFEA362),
                description = "",
                isSelected = false
            )
        ),
        onLabelSelect = { _, _ -> },
        onCreateLabel = { },
        onEditLabel = { },
        onDeleteLabel = { }
    )
}