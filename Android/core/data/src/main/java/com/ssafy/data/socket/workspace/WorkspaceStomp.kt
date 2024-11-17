package com.ssafy.data.socket.workspace

import android.util.Log
import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.socket.BaseStompManager
import com.ssafy.data.socket.workspace.service.WorkspaceService
import com.ssafy.network.socket.StompMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.produceIn
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
            runCatching {
                stomp.subscribe("workspace/$workspaceId").buffer(Channel.BUFFERED).produceIn(this)
                    .consumeEach { message ->
                        Log.d("TAG", "consumeEach: $message")
                        handleMessage(message)
                    }
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    private suspend fun handleMessage(message: StompMessage) = when (message.action) {
        "DELETE_WORKSPACE" -> workspaceService.deleteWorkSpace(message.data)
        "EDIT_WORKSPACE" -> workspaceService.editWorkSpace(message.data)
        "ADD_WORKSPACE_MEMBER" -> workspaceService.addMember(message.data)
        "DELETE_WORKSPACE_MEMBER" -> workspaceService.deleteMember(message.data)
        "EDIT_WORKSPACE_MEMBER" -> workspaceService.editMember(message.data)
        "ADD_BOARD" -> workspaceService.addBoard(message.data)
        "EDIT_BOARD" -> workspaceService.editBoard(message.data)
        "DELETE_BOARD" -> workspaceService.deleteBoard(message.data)
        "EDIT_ARCHIVE_BOARD" -> workspaceService.editArchivedBoard(message.data)
        else -> {}
    }

    fun disconnect() {
        _job?.cancel()
        _job = null
    }
}