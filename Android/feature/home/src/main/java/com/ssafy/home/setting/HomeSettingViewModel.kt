package com.ssafy.home.setting

import androidx.lifecycle.viewModelScope
import com.ssafy.home.GetDetailWorkspaceUseCase
import com.ssafy.home.data.DetailWorkspaceData
import com.ssafy.ui.viewmodel.BaseViewModel
import com.ssafy.workspace.DeleteWorkspaceUseCase
import com.ssafy.workspace.UpdateWorkspaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeSettingViewModel @Inject constructor(
    private val getDetailWorkspaceUseCase: GetDetailWorkspaceUseCase,
    private val deleteWorkspaceUseCase: DeleteWorkspaceUseCase,
    private val updateWorkspaceUseCase: UpdateWorkspaceUseCase,
) : BaseViewModel() {

    private val _detailWorkspaceData = MutableStateFlow(DetailWorkspaceData(-1, "", emptyList()))
    val settingData = _detailWorkspaceData.asStateFlow()

    fun getSettingInfo(workspaceId: Long) = viewModelScope.launch(Dispatchers.IO) {
        getDetailWorkspaceUseCase(workspaceId).safeCollect { data ->
            data?.let { _detailWorkspaceData.emit(it) }
        }
    }

    fun deleteWorkspace(
        workspaceId: Long,
        backHome: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            deleteWorkspaceUseCase(workspaceId, isConnected).safeCollect {
                withMain { backHome() }
            }
        }
    }

    fun updateWorkspaceName(
        workspaceId: Long,
        name: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        withSocketState { isConnected ->
            updateWorkspaceUseCase(workspaceId, name, isConnected).safeCollect()
        }
    }

}
