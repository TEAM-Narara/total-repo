package com.ssafy.home.home

import androidx.lifecycle.viewModelScope
import com.ssafy.home.CreateWorkSpaceUseCase
import com.ssafy.home.GetHomeInfoUseCase
import com.ssafy.home.data.HomeData
import com.ssafy.logout.LogoutUseCase
import com.ssafy.ui.networkstate.NetworkState
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getHomeInfoUseCase: GetHomeInfoUseCase,
    private val createWorkSpaceUseCase: CreateWorkSpaceUseCase
) : BaseViewModel() {

    private val _homeData: MutableStateFlow<HomeData> = MutableStateFlow(HomeData())
    val homeData: StateFlow<HomeData> = _homeData.asStateFlow()

    fun logout(onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        logoutUseCase.logout().withUiState().collect {
            withMain { onSuccess() }
        }
    }

    fun getHomeInfo() = viewModelScope.launch(Dispatchers.IO) {
        val isConnected = NetworkState.isConnected.value
        getHomeInfoUseCase(isConnected).withUiState().collect { _homeData.emit(it) }
    }

    fun chaneSelectedWorkSpace(workSpaceId: Long) = viewModelScope.launch(Dispatchers.IO) {
        getHomeInfoUseCase(homeData.value, workSpaceId).withUiState().collect { _homeData.emit(it) }
    }

    fun createWorkSpace() = viewModelScope.launch(Dispatchers.IO) {
        val isConnected = NetworkState.isConnected.value
        createWorkSpaceUseCase(isConnected).withUiState().collect()
    }

}
