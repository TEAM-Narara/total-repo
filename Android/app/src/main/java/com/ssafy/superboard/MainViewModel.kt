package com.ssafy.superboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.logout.LogoutUseCase
import com.ssafy.model.manager.AuthManager
import com.ssafy.model.manager.ConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logOutUseCase: LogoutUseCase
) : ViewModel() {

    init {
        checkAuthEvent()
    }

    private val _authEvent = MutableSharedFlow<Boolean>()
    val authEvent: SharedFlow<Boolean> = _authEvent.asSharedFlow()

    private fun checkAuthEvent() = viewModelScope.launch(Dispatchers.IO) {
        AuthManager.noAuthEvent.collect {
            logOutUseCase().collect {
                _authEvent.emit(true)
            }
        }
    }

}
