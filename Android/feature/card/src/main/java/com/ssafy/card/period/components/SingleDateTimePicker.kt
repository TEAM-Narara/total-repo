package com.ssafy.card.period.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.values.Transparent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SingleDateTimePicker(
    dateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    validationPredicate: ((LocalDateTime) -> Boolean)? = null,
    dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
) {
    val context = LocalContext.current

    Box(modifier = modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        val currentDateTime = dateTime ?: LocalDateTime.now()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                TimePickerDialog(
                    context,
                    { _, hourOfDay: Int, minute: Int ->
                        val selectedDateTime = LocalDateTime.of(
                            year, month + 1, dayOfMonth,
                            hourOfDay, minute
                        )
                        if (validationPredicate?.invoke(selectedDateTime) != false) {
                            onDateTimeSelected(selectedDateTime)
                        }
                    },
                    currentDateTime.hour,
                    currentDateTime.minute,
                    true
                ).show()
            },
            currentDateTime.year,
            currentDateTime.monthValue - 1,
            currentDateTime.dayOfMonth
        ).show()
    }) {
        OutlinedTextField(
            value = dateTime?.format(dateTimeFormatter) ?: "",
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors().copy(
                disabledContainerColor = Transparent,
                disabledTextColor = LocalContentColor.current,
                disabledLabelColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}


@Preview
@Composable
private fun SingleDateTimePickerPrev() {
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    SingleDateTimePicker(
        dateTime = selectedDateTime,
        onDateTimeSelected = { selectedDateTime = it },
        label = "날짜/시간 선택"
    )
}