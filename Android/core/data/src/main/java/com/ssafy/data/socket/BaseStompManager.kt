package com.ssafy.data.socket

import android.util.Log
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.socket.AckMessage
import com.ssafy.network.BuildConfig
import com.ssafy.model.socket.ConnectionState
import com.ssafy.network.socket.StompClientManager
import com.ssafy.network.socket.StompData
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
        val lastOffset = dataStoreRepository.getStompOffset(topic)

        val dataHandler = StompDataHandler<StompData>(lastOffset) { response ->
            val ack = AckMessage(
                offset = response.offset,
                topic = topic.split("/").joinToString("-"),
                partition = response.partition,
                groupId = "member-$memberId"
            )

            stompClientManager.send(SOCKET_ID, ACK_URL, ack)
            Log.d("TAG", "ack: $ack")

            dataStoreRepository.saveStompOffset(topic, response.offset)

            emit(response.data)
        }

        state.collect {
            when (it) {
                ConnectionState.Connected -> stompClientManager.subscribe(
                    SOCKET_ID,
                    "/topic/$topic/member/$memberId",
                    StompData::class.java
                ).collect(dataHandler::handleSocketData)

                ConnectionState.Disconnected -> connect()

                else -> {}
            }
        }
    }

    suspend fun connect() {
        stompClientManager.connect(SOCKET_ID, SOCKET_URL) {
            Log.d("TAG", "subscribe: Socket is connected")
            // TODO : 데이터 동기화 작업 추가하기
        }
    }

    companion object {
        private const val SOCKET_ID = "SUPER_BOARD"
        private const val BASE_URL = BuildConfig.BASE_URL
        private const val SOCKET_URL = "$BASE_URL/ws/websocket"
        private const val ACK_URL = "/app/ack"
    }
}