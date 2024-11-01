package com.ssafy.login.login

import androidx.lifecycle.viewModelScope
import com.ssafy.login.BuildConfig
import com.ssafy.login.LogInUseCase
import com.ssafy.model.user.OAuth
import com.ssafy.model.user.User
import com.ssafy.model.user.github.GitHubDTO
import com.ssafy.ui.uistate.UiState
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val loginUseCase: LogInUseCase
) : BaseViewModel() {

    fun loginWithGitHub(
        code: String,
        onSuccess: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val gitHubDTO = GitHubDTO(BuildConfig.GIT_ID, BuildConfig.GIT_SECRET, code)
        loginUseCase(gitHubDTO).withUiState().collect { isSuccess ->
            if (isSuccess) withMain(onSuccess)
        }
    }

    fun loginWithNaver(
        token: String,
        onSuccess: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        loginUseCase(OAuth.Naver(token)).withUiState().collect { isSuccess ->
            if (isSuccess) withMain(onSuccess)
        }
    }

    fun failToLoginWithOauth(message: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.emit(UiState.Error(message))
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        loginUseCase(email = email, password = password).withUiState().collect { isSuccess ->
            if (isSuccess) withMain(onSuccess)
        }
    }

}
