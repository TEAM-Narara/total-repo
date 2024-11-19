package com.ssafy.socket

import com.ssafy.data.socket.BaseStompManager
import com.ssafy.model.socket.ConnectionState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSocketStateUseCase @Inject constructor(
    private val baseStompManager: BaseStompManager
) {
    operator fun invoke(): StateFlow<ConnectionState> {
        return baseStompManager.state
    }
}
