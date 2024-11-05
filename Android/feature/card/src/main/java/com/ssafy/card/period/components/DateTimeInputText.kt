package com.ssafy.card.period.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.formatTimestamp
import com.ssafy.designsystem.values.Transparent

@Composable
fun DateTimeInputText(
    initialSelectedDateMillis: Long?,
    onDateTimeSelected: (Long?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    var isDateTimePickerShow by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { isDateTimePickerShow = true }
    ) {
        OutlinedTextField(
            value = initialSelectedDateMillis?.formatTimestamp() ?: "",
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Transparent,
                disabledTextColor = LocalContentColor.current,
                disabledLabelColor = MaterialTheme.colorScheme.primary
            )
        )
    }

    if (isDateTimePickerShow) DateTimePicker(
        selectedDateTimeMillis = initialSelectedDateMillis,
        onDismiss = { isDateTimePickerShow = false },
        onConfirm = onDateTimeSelected,
    )
}


@Preview
@Composable
private fun SingleDateTimePickerPrev() {
    var selectedDateTime by remember { mutableStateOf<Long?>(null) }
    DateTimeInputText(
        initialSelectedDateMillis = selectedDateTime,
        onDateTimeSelected = { selectedDateTime = it },
        label = "날짜/시간 선택"
    )
}