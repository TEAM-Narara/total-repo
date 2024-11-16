package com.ssafy.socket

import com.ssafy.data.socket.BaseStompManager
import javax.inject.Inject

class ConnectSocketUseCase @Inject constructor(
    private val baseStompManager: BaseStompManager
) {
    suspend operator fun invoke() {
        return baseStompManager.connect()
    }
}