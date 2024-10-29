package com.ssafy.login.login

import androidx.lifecycle.viewModelScope
import com.ssafy.model.user.User
import com.ssafy.ui.uistate.UiState
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor() : BaseViewModel() {

    private val _loginState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Success)
    val loginState = _loginState.asStateFlow()

    fun successToLoginWithNaver(user: User) = viewModelScope.launch(Dispatchers.IO) {
      // TODO 로그인 로직 구현
    }

    fun failToLoginWithNaver(message: String) = viewModelScope.launch(Dispatchers.IO) {
        _loginState.emit(UiState.Error(message))
    }

}
