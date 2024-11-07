package com.ssafy.data.socket

import com.ssafy.network.socket.ConnectionState
import com.ssafy.network.socket.StompClientManager
import com.ssafy.network.socket.StompResponse
import kotlinx.coroutines.flow.flow

open class BaseStomp(private val stompClientManager: StompClientManager) {
    val state = stompClientManager.observeConnectionState(SOCKET_ID)

    protected suspend fun subScribe(topic: String) = flow {
        state.collect {
            when (it) {
                ConnectionState.Connected -> stompClientManager.subscribe(
                    SOCKET_ID,
                    topic,
                    StompResponse::class.java
                ).collect(::emit)

                ConnectionState.Disconnected -> stompClientManager.connect(SOCKET_ID, BASE_URL)

                else -> {}
            }
        }
    }

    companion object {
        private const val SOCKET_ID = "SUPER_BOARD"
        private const val BASE_URL = "/ws"
    }
}