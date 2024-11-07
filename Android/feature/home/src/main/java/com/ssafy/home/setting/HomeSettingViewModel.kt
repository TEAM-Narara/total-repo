package com.ssafy.home.setting

import androidx.lifecycle.viewModelScope
import com.ssafy.home.GetDetailWorkspaceUseCase
import com.ssafy.home.data.SettingData
import com.ssafy.ui.networkstate.NetworkState
import com.ssafy.ui.viewmodel.BaseViewModel
import com.ssafy.workspace.DeleteWorkspaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeSettingViewModel @Inject constructor(
    private val getDetailWorkspaceUseCase: GetDetailWorkspaceUseCase,
    private val deleteWorkspaceUseCase: DeleteWorkspaceUseCase
) : BaseViewModel() {

    private val _settingData = MutableStateFlow(SettingData(-1, "", emptyList()))
    val settingData = _settingData.asStateFlow()

    fun getSettingInfo(workspaceId: Long) = viewModelScope.launch(Dispatchers.IO) {
        getDetailWorkspaceUseCase(workspaceId).safeCollect { data ->
            data?.let { _settingData.emit(it) }
        }
    }

    fun deleteWorkspace(
        workspaceId: Long,
        backHome: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val isConnected = NetworkState.isConnected.value
        deleteWorkspaceUseCase(workspaceId, isConnected).safeCollect {
            withMain { backHome() }
        }
    }

}
