package com.ssafy.home.home

import androidx.lifecycle.viewModelScope
import com.ssafy.home.GetHomeInfoUseCase
import com.ssafy.home.data.HomeData
import com.ssafy.logout.LogoutUseCase
import com.ssafy.ui.viewmodel.BaseViewModel
import com.ssafy.workspace.CreateWorkspaceUseCase
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
    private val createWorkspaceUseCase: CreateWorkspaceUseCase,
) : BaseViewModel() {

    private var workspaceId: Long? = null
    private val _homeData: MutableStateFlow<HomeData> = MutableStateFlow(HomeData())
    val homeData: StateFlow<HomeData> = _homeData.asStateFlow()

    fun logout(onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        logoutUseCase.logout().withUiState().collect {
            withMain { onSuccess() }
        }
    }

    fun getHomeInfo() = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            getHomeInfoUseCase(isConnected, workspaceId).safeCollect { _homeData.emit(it) }
        }
    }

    fun changeSelectedWorkSpace(newWorkspaceId: Long) = viewModelScope.launch(Dispatchers.IO) {
        workspaceId = newWorkspaceId
        getHomeInfoUseCase(homeData.value, newWorkspaceId).safeCollect { homeData ->
            homeData?.let { _homeData.emit(it) }
        }
    }

    fun createWorkSpace() = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            createWorkspaceUseCase(isConnected).withUiState().collect()
        }
    }

}
