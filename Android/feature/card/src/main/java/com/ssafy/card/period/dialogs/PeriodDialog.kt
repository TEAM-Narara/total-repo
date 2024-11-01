package com.ssafy.card.period.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssafy.card.period.components.DateTimeInputText
import com.ssafy.card.period.data.PeriodData
import com.ssafy.designsystem.dialog.BaseDialog
import com.ssafy.designsystem.dialog.DialogState
import com.ssafy.designsystem.values.PaddingSmall

@Composable
fun PeriodDialog(
    modifier: Modifier = Modifier,
    dialogState: DialogState<PeriodData>,
    onConfirm: (PeriodData) -> Unit
) {
    val (startDateTime, setStartDateTime) = remember(dialogState.isVisible) { mutableStateOf(dialogState.parameter?.startDate) }
    val (endDateTime, setEndDateTime) = remember(dialogState.isVisible) { mutableStateOf(dialogState.parameter?.endDate) }
    val isValidDateTimeRange = remember(startDateTime, endDateTime) {
        when {
            startDateTime == null || endDateTime == null -> true
            startDateTime <= endDateTime -> true
            else -> false
        }
    }

    BaseDialog(
        modifier = modifier,
        dialogState = dialogState,
        title = "기간 설정",
        validation = { isValidDateTimeRange },
        onConfirm = { onConfirm(PeriodData(startDateTime, endDateTime)) },
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(PaddingSmall)
        ) {
            DateTimeInputText(
                initialSelectedDateMillis = startDateTime,
                onDateTimeSelected = setStartDateTime,
                label = "시작 날짜/시간",
            )

            DateTimeInputText(
                initialSelectedDateMillis = endDateTime,
                onDateTimeSelected = setEndDateTime,
                label = "종료 날짜/시간",
            )

            if (!isValidDateTimeRange) {
                Text(
                    text = "시작 시간은 종료 시간보다 이후일 수 없습니다.",
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}