package com.ssafy.card.label.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.card.label.data.LabelData
import com.ssafy.designsystem.component.IconButton
import com.ssafy.designsystem.getContrastingTextColor
import com.ssafy.designsystem.values.CornerSmall
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.TextSmall

@Composable
fun EditableLabelItem(
    modifier: Modifier = Modifier,
    labelData: LabelData,
    onLabelSelect: (Long, Boolean) -> Unit,
    onEditLabel: (LabelData) -> Unit,
    onDeleteLabel: (Long) -> Unit,
) {
    EditableLabelItem(
        modifier = modifier,
        selected = labelData.isSelected,
        color = Color(labelData.color),
        description = labelData.description,
        onLabelClicked = { onLabelSelect(labelData.id, it) },
        onEditLabel = { onEditLabel(labelData) },
        onDeleteLabel = { onDeleteLabel(labelData.id) }
    )
}

@Composable
fun EditableLabelItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    color: Color,
    description: String,
    onLabelClicked: (Boolean) -> Unit,
    onEditLabel: () -> Unit,
    onDeleteLabel: () -> Unit,
) {
    var isSelected by remember { mutableStateOf(selected) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .background(
                    color = color,
                    shape = RoundedCornerShape(CornerSmall),
                )
                .clickable {
                    isSelected = !isSelected
                    onLabelClicked(!isSelected)
                }
                .padding(PaddingSmall),
        ) {
            Text(
                text = description,
                fontSize = TextSmall,
                color = getContrastingTextColor(color),
                overflow = TextOverflow.Ellipsis,
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "isSelected",
                )
            } else {
                Spacer(modifier = Modifier.height(IconMedium))
            }
        }
        IconButton(onClick = onEditLabel, imageVector = Icons.Outlined.Edit)
        IconButton(onClick = onDeleteLabel, imageVector = Icons.Outlined.Delete)
    }
}

@Preview
@Composable
private fun EditableLabelItemPreviewIsSelected() {
    EditableLabelItem(
        labelData = LabelData(
            id = 0L,
            color = 0xFF4BCE97,
            description = "success",
            isSelected = true
        ),
        onLabelSelect = { _, _ -> },
        onEditLabel = {},
        onDeleteLabel = {}
    )
}

@Preview
@Composable
private fun EditableLabelItemPreviewIsNotSelected() {
    EditableLabelItem(
        labelData = LabelData(
            id = 0L,
            color = 0xFF4BCE97,
            description = "success",
            isSelected = false
        ),
        onLabelSelect = { _, _ -> },
        onEditLabel = {},
        onDeleteLabel = {}
    )
}