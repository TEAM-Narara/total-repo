package com.ssafy.network.socket

import android.util.Log
import com.google.gson.Gson
import com.ssafy.model.socket.ConnectionState
import com.ssafy.network.module.AuthInterceptorOkHttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

private const val TAG = "StompClientManager"

@Singleton
class StompClientManager @Inject constructor(
    @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    private val client = StompClient(OkHttpWebSocketClient(okHttpClient))
    private val sessions = mutableMapOf<String, StompSession>()
    private val connectionStates = mutableMapOf<String, MutableStateFlow<ConnectionState>>()

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
                sessions.remove(id)
            }
        }
    }

    fun observeConnectionState(id: String): StateFlow<ConnectionState> = connectionStates[id]
        ?: MutableStateFlow<ConnectionState>(ConnectionState.Disconnected).also {
            connectionStates[id] = it
        }

    private fun updateConnectionState(id: String, state: ConnectionState) =
        connectionStates[id]?.update { state } ?: MutableStateFlow(state).also {
            connectionStates[id] = it
        }

    suspend fun subscribe(
        id: String,
        topic: String,
    ): Flow<StompResponse> {
        Log.i(TAG, "subscribe: $id $topic")
        val session = sessions[id] ?: throw Exception("연결된 소켓이 없습니다.")
        return session.subscribe(topic).map {
            Log.i(TAG, "<------ receive $topic")
            Log.i(TAG, "offset : ${it.headers["offset"]?.toLong()}")
            Log.i(TAG, "data: ${it.bodyAsText}")
            Log.i(TAG, "<------ receive end")

            StompResponse(
                offset = it.headers["offset"]?.toLong() ?: throw Exception("offset이 존재하지 않습니다."),
                partition = 0, // it.headers["partition"]?.toLong() ?: 0,
                type = it.headers["type"] ?: "RECEIVED",
                data = it.bodyAsText
            )
        }.catch { exception ->
            Log.e(TAG, "StompClientManager: Exception $exception")
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
        Log.i(TAG, "------> send $url")
        Log.i(TAG, "data: ${gson.toJson(body)}")
        Log.i(TAG, "------> send end")
        sessions[id]?.sendText(url, gson.toJson(body))
    }
}