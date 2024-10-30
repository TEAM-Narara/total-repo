package com.ssafy.login.login

import androidx.lifecycle.viewModelScope
import com.ssafy.model.user.User
import com.ssafy.ui.uistate.UiState
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor() : BaseViewModel() {

    fun successToLoginWithNaver(user: User) = viewModelScope.launch(Dispatchers.IO) {
        // TODO 로그인 로직 구현
        _uiState.emit(UiState.Error("로그인 성공"))
    }

    fun failToLoginWithNaver(message: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.emit(UiState.Error(message))
    }

}
