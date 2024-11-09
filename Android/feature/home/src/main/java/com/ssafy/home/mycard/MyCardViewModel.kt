package com.ssafy.home.mycard

import androidx.lifecycle.ViewModel
import com.ssafy.socket.GetSocketStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyCardViewModel @Inject constructor(
    getSocketStateUseCase: GetSocketStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyCardUiState())
    val uiState = _uiState.asStateFlow()
}
