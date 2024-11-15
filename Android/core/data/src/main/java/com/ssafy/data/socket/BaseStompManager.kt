package com.ssafy.data.socket

import android.util.Log
import com.google.gson.Gson
import com.ssafy.data.repository.sync.SyncRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.manager.ConnectManager
import com.ssafy.model.socket.AckMessage
import com.ssafy.model.socket.ConnectionState
import com.ssafy.network.BuildConfig
import com.ssafy.network.socket.StompClientManager
import com.ssafy.network.socket.StompFetchMessage
import com.ssafy.network.socket.StompMessage
import com.ssafy.network.socket.StompResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseStompManager @Inject constructor(
    private val stompClientManager: StompClientManager,
    private val dataStoreRepository: DataStoreRepository,
    private val syncRepository: SyncRepository,
    private val gson: Gson
) {
    val state = stompClientManager.observeConnectionState(SOCKET_ID)

    suspend fun subscribe(topic: String) = flow {
        val memberId = dataStoreRepository.getUser().memberId
        val lastOffset = dataStoreRepository.getStompOffset(topic)

        val dataHandler = StompDataHandler(
            lastOffset,
            object : StompDataHandler.Callback {
                override suspend fun ack(response: StompResponse) {
                    when (response.type) {
                        "RECEIVED" -> stompClientManager.send(
                            SOCKET_ID, ACK_URL, AckMessage(
                                offset = response.offset,
                                topic = topic.split("/").joinToString("-"),
                                partition = response.partition,
                                groupId = "member-$memberId"
                            )
                        )

                        "FETCHED" -> stompClientManager.send(
                            SOCKET_ID, ACK_LAST_URL, AckMessage(
                                offset = gson.fromJson(response.data, List::class.java).map {
                                    gson.fromJson(it.toString(), StompFetchMessage::class.java)
                                }.last().offset,
                                topic = topic.split("/").joinToString("-"),
                                partition = response.partition,
                                groupId = "member-$memberId"
                            )
                        )

                        else -> return
                    }

                    dataStoreRepository.saveStompOffset(topic, response.offset)
                }

                override suspend fun onDataReleased(response: StompResponse) {
                    when (response.type) {
                        "RECEIVED" -> emit(gson.fromJson(response.data, StompMessage::class.java))
                        "FETCHED" -> gson.fromJson(response.data, List::class.java).forEach {
                            emit(gson.fromJson(it.toString(), StompFetchMessage::class.java).message)
                        }
                    }
                }

                override fun onTimeout(lastOffset: Long) {
                    TODO("Timeout시 http 요청 보내기")
                }
            }
        )

        state.collect {
            when (it) {
                ConnectionState.Connected -> stompClientManager.subscribe(
                    SOCKET_ID,
                    "/topic/$topic/member/$memberId",
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
        private const val ACK_LAST_URL = "/app/ack/last"
    }
}