package com.ssafy.data.socket

import android.util.Log
import com.ssafy.data.repository.sync.SyncRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.manager.ConnectManager
import com.ssafy.model.socket.AckMessage
import com.ssafy.network.BuildConfig
import com.ssafy.model.socket.ConnectionState
import com.ssafy.network.socket.StompClientManager
import com.ssafy.network.socket.StompData
import com.ssafy.network.socket.StompResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseStompManager @Inject constructor(
    private val stompClientManager: StompClientManager,
    private val dataStoreRepository: DataStoreRepository,
    private val syncRepository: SyncRepository
) {
    val state = stompClientManager.observeConnectionState(SOCKET_ID)

    suspend fun subscribe(topic: String) = flow {
        val memberId = dataStoreRepository.getUser().memberId
        val lastOffset = dataStoreRepository.getStompOffset(topic)

        val dataHandler = StompDataHandler<StompData>(
            lastOffset,
            object : StompDataHandler.Callback<StompData> {
                override suspend fun ack(data: StompResponse<StompData>) {
                    val ack = AckMessage(
                        offset = data.offset,
                        topic = topic.split("/").joinToString("-"),
                        partition = data.partition,
                        groupId = "member-$memberId"
                    )

                    stompClientManager.send(SOCKET_ID, ACK_URL, ack)
                    dataStoreRepository.saveStompOffset(topic, data.offset)
                }

                override suspend fun onDataReleased(data: StompResponse<StompData>) {
                    emit(data.data)
                }
            }
        )

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
            ConnectManager.sendConnectingEvent(true)
            syncRepository.syncAll()
            Log.d("TAG", "subscribe: Sync all")
            ConnectManager.sendConnectingEvent(false)
        }
    }

    companion object {
        private const val SOCKET_ID = "SUPER_BOARD"
        private const val BASE_URL = BuildConfig.BASE_URL
        private const val SOCKET_URL = "$BASE_URL/ws/websocket"
        private const val ACK_URL = "/app/ack"
    }
}