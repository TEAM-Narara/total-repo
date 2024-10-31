package com.ssafy.login.login

import androidx.lifecycle.viewModelScope
import com.ssafy.ui.uistate.UiState
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor() : BaseViewModel() {

    fun successToLoginWithGitHub(code: String) = viewModelScope.launch(Dispatchers.IO) {
        // TODO 로그인 로직 구현
        // TODO GitHub는 code를 받아서 토큰을 받아오고, 토크을 이용해 사용자 정보를 받아옵니다.
        _uiState.emit(UiState.Error(code))
    }

    fun successToLoginWithNaver(token: String) = viewModelScope.launch(Dispatchers.IO) {
        // TODO 로그인 로직 구현
        // TODO Naver는 토큰을 받아서 사용자 정보를 받아옵니다. 코드를 사용하지 않는 이유는
        // TODO Naver는 이미 토큰을 받아와서 코드를 재사용 할 수 없습니다.
        _uiState.emit(UiState.Error(token))
    }

    fun failToLoginWithOauth(message: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.emit(UiState.Error(message))
    }

}
