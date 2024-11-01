package com.ssafy.card.period.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ssafy.designsystem.values.CornerLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.ReversePrimary
import com.ssafy.designsystem.values.White
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import com.ssafy.designsystem.millisecondsToZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    selectedDateTimeMillis: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit,
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateTimeMillis)
    val timePickerState = rememberTimePickerState(
        initialHour = selectedDateTimeMillis?.millisecondsToZonedDateTime()?.hour ?: 0,
        initialMinute = selectedDateTimeMillis?.millisecondsToZonedDateTime()?.minute ?: 0
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        colors = DatePickerDefaults.colors(containerColor = White),
        shape = RoundedCornerShape(CornerLarge)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = PaddingDefault)
        ) {
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = White,
                    selectedDayContainerColor = Primary,
                    selectedDayContentColor = White,
                    selectedYearContainerColor = Primary,
                    selectedYearContentColor = White,
                    todayContentColor = Primary,
                    todayDateBorderColor = Primary
                )
            )

            TimeInput(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    selectorColor = White,
                    containerColor = White,
                    periodSelectorBorderColor = Primary,
                    clockDialSelectedContentColor = Primary,
                    periodSelectorSelectedContainerColor = Primary,
                    periodSelectorSelectedContentColor = White,
                    periodSelectorUnselectedContainerColor = White,
                    periodSelectorUnselectedContentColor = Primary,
                    timeSelectorSelectedContainerColor = Primary,
                    timeSelectorSelectedContentColor = White,
                    timeSelectorUnselectedContainerColor = White,
                    timeSelectorUnselectedContentColor = Primary,
                )
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingDefault),) {
                TextButton(onClick = {
                    onConfirm(null)
                    onDismiss()
                }) {
                    Text("초기화", color = ReversePrimary)
                }
                Spacer(modifier = modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text("취소", color = ReversePrimary)
                }
                TextButton(onClick = {
                    onConfirm(datePickerState.selectedDateMillis?.let { it + timePickerState.selectedTimeMillis })
                    onDismiss()
                }) {
                    Text("확인", color = Primary)
                }
            }
        }
    }
}