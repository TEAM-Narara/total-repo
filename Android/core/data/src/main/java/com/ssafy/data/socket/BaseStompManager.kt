package com.ssafy.data.socket

import android.util.Log
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.network.BuildConfig
import com.ssafy.network.socket.ConnectionState
import com.ssafy.network.socket.StompClientManager
import com.ssafy.network.socket.StompResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseStompManager @Inject constructor(
    private val stompClientManager: StompClientManager,
    private val dataStoreRepository: DataStoreRepository,
) {
    val state = stompClientManager.observeConnectionState(SOCKET_ID)

    suspend fun subscribe(topic: String) = flow {
        val memberId = dataStoreRepository.getUser().memberId
        state.collect {
            when (it) {
                ConnectionState.Connected -> stompClientManager.subscribe(
                    SOCKET_ID,
                    "/topic/$topic/member/$memberId",
                    StompResponse::class.java
                ).collect(::emit)

                ConnectionState.Disconnected -> stompClientManager.connect(
                    SOCKET_ID,
                    BASE_URL
                ) {
                    Log.d("TAG", "subscribe: Socket is connected")
                    // TODO : 데이터 동기화 작업 추가하기
                }

                else -> {}
            }
        }
    }

    companion object {
        private const val SOCKET_ID = "SUPER_BOARD"
        private const val BASE_URL = "${BuildConfig.BASE_URL}/ws/websocket"
    }
}