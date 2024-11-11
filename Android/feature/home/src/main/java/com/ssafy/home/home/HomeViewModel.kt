package com.ssafy.home.home

import androidx.lifecycle.viewModelScope
import com.ssafy.home.GetHomeInfoUseCase
import com.ssafy.home.data.HomeData
import com.ssafy.logout.LogoutUseCase
import com.ssafy.model.socket.ConnectionState
import com.ssafy.ui.viewmodel.BaseViewModel
import com.ssafy.workspace.CreateWorkspaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getHomeInfoUseCase: GetHomeInfoUseCase,
    private val createWorkspaceUseCase: CreateWorkspaceUseCase,
) : BaseViewModel() {

    private val selectedWorkspaceId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeData by lazy {
        combine(
            selectedWorkspaceId,
            socketState,
        ) { workspaceId, isConnected ->
            if (isConnected != ConnectionState.Connected) null
            else getHomeInfoUseCase(true, workspaceId)
        }.filterNotNull()
            .flatMapLatest { homeDataFlow: Flow<HomeData> -> homeDataFlow }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
    }

    fun updateSelectedWorkspace(id: Long) = selectedWorkspaceId.update { id }

    fun createWorkspace() = withIO {
        withSocketState { isConnected: Boolean ->
            createWorkspaceUseCase(isConnected).withUiState().collect()
        }
    }

    fun logout(onSuccess: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        logoutUseCase.logout().withUiState().collect {
            withMain { onSuccess() }
        }
    }

}
