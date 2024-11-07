package com.ssafy.data.socket.workspace

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.socket.BaseStomp
import com.ssafy.data.socket.workspace.service.WorkspaceService
import com.ssafy.network.socket.StompClientManager
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Singleton
class WorkspaceStomp @Inject constructor(
    stompClientManager: StompClientManager,
    private val workspaceService: WorkspaceService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BaseStomp(stompClientManager) {
    private var _job: Job? = null

    fun connect(workspaceId: Long) {
        disconnect()
        _job = CoroutineScope(ioDispatcher).launch {
            subScribe("WORKSPACE/$workspaceId").stateIn(this).collect {
                when (it.action) {
                    "DELETE_WORKSPACE" -> workspaceService.deleteWorkSpace(it.data)
                    "EDIT_WORKSPACE" -> workspaceService.editWorkSpace(it.data)
                    "ADD_MEMBER" -> workspaceService.addMember(it.data)
                    "DELETE_MEMBER" -> workspaceService.deleteMember(it.data)
                    "EDIT_MEMBER" -> workspaceService.editMember(it.data)
                    "ADD_BOARD" -> workspaceService.addBoard(it.data)
                    "DELETE_BOARD" -> workspaceService.deleteBoard(it.data)
                    else -> {}
                }
            }
        }
    }

    fun disconnect() {
        _job?.cancel()
        _job = null
    }
}