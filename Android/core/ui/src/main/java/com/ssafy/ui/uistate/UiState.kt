package com.ssafy.ui.uistate

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    class Success<T>(val data: T) : UiState<T>()
    class Error(val error: Any) : UiState<Nothing>()
}