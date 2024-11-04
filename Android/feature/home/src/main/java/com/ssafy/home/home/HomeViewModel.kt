package com.ssafy.home.home

import androidx.lifecycle.viewModelScope
import com.ssafy.logout.LogoutUseCase
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel() {

    fun logout(onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        logoutUseCase().withUiState().collect {
            withMain { onSuccess() }
        }
    }
}
