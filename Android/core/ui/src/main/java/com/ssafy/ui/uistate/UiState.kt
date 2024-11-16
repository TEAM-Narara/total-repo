package com.ssafy.ui.uistate

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data class Error(val message: String?) : UiState()

    val isLoading get() = this is Loading
    val isSuccess get() = this is Success
    val isError get() = this is Error

    val errorMessage get() = when(this) {
        is Error -> message
        else -> null
    }
}
