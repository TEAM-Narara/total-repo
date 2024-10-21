package com.ssafy.home.home

import androidx.compose.runtime.Immutable

@Immutable
data class HomeUiState(
    // TODO Board에 대한 정보를 담는 필드를 추가합니다.
    val boards: List<Any> = List(4) { Any() },
)
