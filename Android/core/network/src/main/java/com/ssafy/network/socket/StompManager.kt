package com.ssafy.network.socket

import com.ssafy.network.module.AuthInterceptorOkHttpClient
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

@Singleton
class StompClientManager @Inject constructor(
    @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient,
) {
    private val client = StompClient(OkHttpWebSocketClient(okHttpClient))
    private val sessions = mutableMapOf<String, StompSession>()
    private val subscriptions = mutableMapOf<String, Flow<StompFrame.Message>>()
    private val connectionStates = MutableStateFlow<Map<String, ConnectionState>>(emptyMap())

    suspend fun connect(id: String, url: String) {
        if (sessions[id] == null) {
            val session = client.connect(url)
            sessions[id] = session
        }
        sessions[id]?.let { session ->
            subscriptions[id] = session.subscribe(StompSubscribeHeaders("/topic/$id"))
        }
    }

    fun observeConnectionState(id: String): Flow<ConnectionState> = connectionStates.map {
        it[id] ?: ConnectionState.Disconnected
    }
}