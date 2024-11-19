package com.ssafy.card.period.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState

@OptIn(ExperimentalMaterial3Api::class)
val TimePickerState.selectedTimeMillis: Long get() = (hour * 60 + minute) * 60000L