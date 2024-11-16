package com.ssafy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.model.socket.ConnectionState
import com.ssafy.socket.ConnectSocketUseCase
import com.ssafy.socket.GetSocketStateUseCase
import com.ssafy.ui.uistate.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
open class BaseViewModel : ViewModel() {
    val uiState: StateFlow<UiState> get() = _uiState
    protected val _uiState = MutableStateFlow<UiState>(UiState.Idle)

    @Inject
    lateinit var getSocketStateUseCase: GetSocketStateUseCase

    @Inject
    lateinit var connectSocketUseCase: ConnectSocketUseCase

    val socketState by lazy {
        viewModelScope.launch { connectSocketUseCase() }
        getSocketStateUseCase().filterNotNull().flatMapLatest {
            when (it) {
                ConnectionState.Disconnected, ConnectionState.Connecting -> _uiState.update { UiState.Loading }
                else -> _uiState.update { UiState.Success }
            }
            flow { emit(it) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConnectionState.Disconnected,
        )
    }

    fun <T> Flow<T>.withUiState(onError: (Throwable) -> Unit = {}) = this.onStart {
        _uiState.update { UiState.Loading }
        // 너무 빨리 로딩이 끝나면 collectLatest이기 때문에 로딩 상태를 감지하지 못함
        delay(100)
    }.onCompletion { cause ->
        if (cause == null) _uiState.update { UiState.Success }
    }.catch { exception ->
        exception.printStackTrace()
        _uiState.update { UiState.Error(exception.message) }
        onError(exception)
    }

    fun resetUiState() {
        _uiState.update { UiState.Idle }
    }

    suspend fun <T> Flow<T>.safeCollect(block: suspend (T) -> Unit = {}) {
        runCatching {
            collect { value -> block(value) }
        }.onFailure { error ->
            error.printStackTrace()
            _uiState.update { UiState.Error(error.message) }
        }
    }

    fun withMain(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            block()
        }
    }

    protected fun withIO(block: suspend () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        block()
    }

    suspend fun withSocketState(block: suspend (isConnected: Boolean) -> Unit) {
        socketState.filter {
            it != ConnectionState.Disconnected && it != ConnectionState.Connecting
        }.take(1).collect {
            block(it == ConnectionState.Connected)
        }
    }
}
