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
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun PeriodDialog(
    modifier: Modifier = Modifier,
    dialogState: DialogState<PeriodData>,
    onConfirm: (PeriodData) -> Unit
) {
    var startDateTime by remember {
        mutableStateOf(dialogState.property?.startDate?.let {
            LocalDateTime.ofEpochSecond(
                it,
                0,
                ZoneOffset.UTC
            )
        })
    }
    var endDateTime by remember {
        mutableStateOf(dialogState.property?.endDate?.let {
            LocalDateTime.ofEpochSecond(
                it,
                0,
                ZoneOffset.UTC
            )
        })
    }

    LaunchedEffect(dialogState.isVisible) {
        startDateTime = dialogState.property?.startDate?.let {
            LocalDateTime.ofEpochSecond(
                it,
                0,
                ZoneOffset.UTC
            )
        }

        endDateTime = dialogState.property?.endDate?.let {
            LocalDateTime.ofEpochSecond(
                it,
                0,
                ZoneOffset.UTC
            )
        }
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