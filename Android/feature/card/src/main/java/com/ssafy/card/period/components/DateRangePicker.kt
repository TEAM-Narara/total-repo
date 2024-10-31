package com.ssafy.card.period.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.values.PaddingSmall
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DateTimeRangePicker(
    startDateTime: LocalDateTime?,
    endDateTime: LocalDateTime?,
    onStartDateTimeSelected: (LocalDateTime?) -> Unit,
    onEndDateTimeSelected: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier,
    dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
) {
    val isValidDateTimeRange = remember(startDateTime, endDateTime) {
        when {
            startDateTime == null || endDateTime == null -> true
            startDateTime.isBefore(endDateTime) || startDateTime.isEqual(endDateTime) -> true
            else -> false
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {
        SingleDateTimePicker(
            dateTime = startDateTime,
            onDateTimeSelected = onStartDateTimeSelected,
            label = "시작 날짜/시간",
            validationPredicate = { selectedDateTime ->
                endDateTime?.let { selectedDateTime.isBefore(it) || selectedDateTime.isEqual(it) } ?: true
            },
            dateTimeFormatter = dateTimeFormatter
        )

        SingleDateTimePicker(
            dateTime = endDateTime,
            onDateTimeSelected = onEndDateTimeSelected,
            label = "종료 날짜/시간",
            validationPredicate = { selectedDateTime ->
                startDateTime?.let { selectedDateTime.isAfter(it) || selectedDateTime.isEqual(it) } ?: true
            },
            dateTimeFormatter = dateTimeFormatter
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

@Preview
@Composable
private fun DateRangePickerPrev() {
    var startDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    DateTimeRangePicker(
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        onStartDateTimeSelected = { startDateTime = it },
        onEndDateTimeSelected = { endDateTime = it }
    )
}