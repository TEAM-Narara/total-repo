package com.ssafy.network.socket

import android.util.Log
import com.google.gson.Gson
import com.ssafy.model.socket.ConnectionState
import com.ssafy.network.module.AuthInterceptorOkHttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribe
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StompClientManager @Inject constructor(
    @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    private val client = StompClient(OkHttpWebSocketClient(okHttpClient))
    private val sessions = mutableMapOf<String, StompSession>()
    private val connectionStates = MutableStateFlow<Map<String, ConnectionState>>(emptyMap())

    suspend fun connect(id: String, url: String, onConnect: suspend () -> Unit = {}) {
        if (sessions[id] == null) {
            try {
                updateConnectionState(id, ConnectionState.Connecting)
                sessions[id] = client.connect(url)
                onConnect()
                updateConnectionState(id, ConnectionState.Connected)
            } catch (e: Exception) {
                e.printStackTrace()
                updateConnectionState(id, ConnectionState.Error(e))
            }
        }
    }

    fun observeConnectionState(id: String): Flow<ConnectionState> = connectionStates.map {
        it[id] ?: ConnectionState.Disconnected
    }

    private fun updateConnectionState(id: String, state: ConnectionState) {
        connectionStates.update { states ->
            states.toMutableMap().apply { put(id, state) }
        }
    }

    suspend fun <T> subscribe(
        id: String,
        topic: String,
        clazz: Class<T>,
        ack: suspend (headers: MutableMap<String, String>) -> Unit = {}
    ): Flow<T> {
        Log.d("TAG", "subscribe: $id $topic")
        val session = sessions[id] ?: throw Exception("연결된 소켓이 없습니다.")
        return session.subscribe(topic).map {
            Log.d("TAG", "data: ${it.bodyAsText}")
            ack(it.headers)
            gson.fromJson(it.bodyAsText, clazz)
        }.catch { exception ->
            exception.printStackTrace()
            sessions.remove(id)
            updateConnectionState(id, ConnectionState.Error(exception))
        }
    }

    suspend fun disconnect(id: String) {
        runCatching {
            sessions[id]?.disconnect()
        }.also {
            sessions.remove(id)
            updateConnectionState(id, ConnectionState.Disconnected)
        }
    }

    suspend fun <T> send(id: String, url: String, body: T) {
        sessions[id]?.sendText(url, gson.toJson(body))
    }
}