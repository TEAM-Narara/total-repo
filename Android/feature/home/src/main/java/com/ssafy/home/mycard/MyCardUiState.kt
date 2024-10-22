package com.ssafy.home.mycard

import androidx.compose.runtime.Immutable

@Immutable
data class MyCardUiState(
    val boards: List<Any> = List(10) { Any() }
)
