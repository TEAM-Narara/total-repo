package com.ssafy.card.period.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ssafy.card.period.components.DateTimeRangePicker
import com.ssafy.card.period.data.PeriodData
import com.ssafy.designsystem.dialog.BaseDialog
import com.ssafy.designsystem.dialog.DialogState
import com.ssafy.designsystem.millisecondsToZonedDateTime
import java.time.ZoneOffset

@Composable
fun PeriodDialog(
    modifier: Modifier = Modifier,
    dialogState: DialogState<PeriodData>,
    onConfirm: (PeriodData) -> Unit
) {
    var startDateTime by remember {
        mutableStateOf(dialogState.parameter?.startDate?.millisecondsToZonedDateTime())
    }
    var endDateTime by remember {
        mutableStateOf(dialogState.parameter?.endDate?.millisecondsToZonedDateTime())
    }

    LaunchedEffect(dialogState.isVisible) {
        startDateTime = dialogState.parameter?.startDate?.millisecondsToZonedDateTime()
        endDateTime = dialogState.parameter?.endDate?.millisecondsToZonedDateTime()
    }

    BaseDialog(
        modifier = modifier,
        dialogState = dialogState,
        title = "기간 설정",
        onConfirm = {
            onConfirm(
                PeriodData(
                    startDateTime?.toEpochSecond(ZoneOffset.UTC),
                    endDateTime?.toEpochSecond(ZoneOffset.UTC)
                )
            )
        },
    ) {
        DateTimeRangePicker(
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            onStartDateTimeSelected = { startDateTime = it },
            onEndDateTimeSelected = { endDateTime = it }
        )
    }
}