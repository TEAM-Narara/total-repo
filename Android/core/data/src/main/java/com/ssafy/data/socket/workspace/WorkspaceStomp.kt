package com.ssafy.data.socket.workspace

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.socket.BaseStompManager
import com.ssafy.data.socket.workspace.service.WorkspaceService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceStomp @Inject constructor(
    private val stomp: BaseStompManager,
    private val workspaceService: WorkspaceService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private var _job: Job? = null
    private var _workspaceId: Long? = null

    fun connect(workspaceId: Long) {
        if (_workspaceId == workspaceId) return

        _workspaceId = workspaceId

        disconnect()

        _job = CoroutineScope(ioDispatcher).launch {
            stomp.subscribe("workspace/$workspaceId").stateIn(this)
                .collect {
                    runCatching {
                        when (it.action) {
                            "DELETE_WORKSPACE" -> workspaceService.deleteWorkSpace(it.data)
                            "EDIT_WORKSPACE" -> workspaceService.editWorkSpace(it.data)
                            "ADD_WORKSPACE_MEMBER" -> workspaceService.addMember(it.data)
                            "DELETE_WORKSPACE_MEMBER" -> workspaceService.deleteMember(it.data)
                            "EDIT_WORKSPACE_MEMBER" -> workspaceService.editMember(it.data)
                            "ADD_BOARD" -> workspaceService.addBoard(it.data)
                            "EDIT_BOARD" -> workspaceService.editBoard(it.data)
                            "DELETE_BOARD" -> workspaceService.deleteBoard(it.data)
                            "EDIT_ARCHIVE_BOARD" -> workspaceService.editArchivedBoard(it.data)
                            else -> {}
                        }
                    }.onFailure { e ->
                        e.printStackTrace()
                    }
                }
        }
    }

    fun disconnect() {
        _job?.cancel()
        _job = null
    }
}