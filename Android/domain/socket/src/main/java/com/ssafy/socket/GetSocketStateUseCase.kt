package com.ssafy.socket

import com.ssafy.data.socket.BaseStompManager
import com.ssafy.model.socket.ConnectionState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSocketStateUseCase @Inject constructor(
    private val baseStompManager: BaseStompManager
) {
    operator fun invoke(): Flow<ConnectionState> {
        return baseStompManager.state
    }
}
