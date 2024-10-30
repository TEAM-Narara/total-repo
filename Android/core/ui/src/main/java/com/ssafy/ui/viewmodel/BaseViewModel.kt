package com.ssafy.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ssafy.ui.uistate.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

open class BaseViewModel : ViewModel() {
    val uiState: StateFlow<UiState> get() = _uiState
    protected val _uiState = MutableStateFlow<UiState>(UiState.Idle)

    fun <T> Flow<T>.withUiState() = this.onStart {
        _uiState.update { UiState.Loading }
    }.onCompletion {
        _uiState.update { UiState.Success }
    }.catch { exception ->
        _uiState.update { UiState.Error(exception.message) }
    }

    fun resetUiState() {
        _uiState.update { UiState.Idle }
    }
}
