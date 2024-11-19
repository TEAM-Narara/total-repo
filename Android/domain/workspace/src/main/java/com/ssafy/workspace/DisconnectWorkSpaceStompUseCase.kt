package com.ssafy.workspace

import com.ssafy.data.socket.workspace.WorkspaceStomp
import javax.inject.Inject

class DisconnectWorkSpaceStompUseCase @Inject constructor(
    private val workspaceStomp: WorkspaceStomp
) {
    operator fun invoke() {
        workspaceStomp.disconnect()
    }
}