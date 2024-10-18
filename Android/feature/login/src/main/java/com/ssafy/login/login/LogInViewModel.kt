package com.ssafy.login.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor() : ViewModel() {
    var uiState = MutableStateFlow(LogInUiState())
        private set

    fun updateEmail(email: String) = uiState.update { it.copy(email = email) }

}
