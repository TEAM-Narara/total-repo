package com.ssafy.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ssafy.ui.uistate.UiState
import kotlinx.coroutines.delay
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
        // 너무 빨리 로딩이 끝나면 collectLatest이기 때문에 로딩 상태를 감지하지 못함
        delay(100)
    }.onCompletion { cause ->
        if (cause == null) _uiState.update { UiState.Success }
    }.catch { exception ->
        exception.printStackTrace()
        _uiState.update { UiState.Error(exception.message) }
    }

    fun resetUiState() {
        _uiState.update { UiState.Idle }
    }
}
