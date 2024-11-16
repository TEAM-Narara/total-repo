package com.ssafy.data.socket

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.data.repository.sync.SyncRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.manager.ConnectManager
import com.ssafy.model.socket.AckMessage
import com.ssafy.model.socket.ConnectionState
import com.ssafy.network.BuildConfig
import com.ssafy.network.api.KafkaAPI
import com.ssafy.network.socket.StompClientManager
import com.ssafy.network.socket.StompFetchMessage
import com.ssafy.network.socket.StompMessage
import com.ssafy.network.socket.StompResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BaseStompManager @Inject constructor(
    private val stompClientManager: StompClientManager,
    private val dataStoreRepository: DataStoreRepository,
    private val syncRepository: SyncRepository,
    private val kafkaAPI: KafkaAPI,
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
                        "RECEIVED" -> {
                            stompClientManager.send(
                                SOCKET_ID, ACK_URL, AckMessage(
                                    offset = response.offset,
                                    topic = topic.split("/").joinToString("-"),
                                    partition = response.partition,
                                    groupId = "member-$memberId"
                                )
                            )
                            dataStoreRepository.saveStompOffset(topic, response.offset)
                        }

                        "FETCHED" -> {
                            val type = object : TypeToken<List<StompFetchMessage>>() {}.type
                            val offset = gson.fromJson<List<StompFetchMessage>>(
                                response.data,
                                type
                            ).last().offset
                            dataStoreRepository.saveStompOffset(topic, offset)

                            stompClientManager.send(
                                SOCKET_ID, ACK_LAST_URL, AckMessage(
                                    offset = offset,
                                    topic = topic.split("/").joinToString("-"),
                                    partition = response.partition,
                                    groupId = "member-$memberId"
                                )
                            )
                        }

                        else -> return
                    }
                }

                override suspend fun onDataReleased(response: StompResponse) {
                    when (response.type) {
                        "RECEIVED" -> emit(gson.fromJson(response.data, StompMessage::class.java))
                        "FETCHED" -> {
                            val type = object : TypeToken<List<StompFetchMessage>>() {}.type
                            gson.fromJson<List<StompFetchMessage>>(
                                response.data,
                                type
                            ).forEach { emit(it.message) }
                        }
                    }
                }

                override fun onTimeout(lastOffset: Long) {
                    val (entityType, primaryId) = topic.split("/")
                    CoroutineScope(Dispatchers.IO).launch {
                        runCatching {
                            kafkaAPI.sync(
                                partition = 0,
                                offset = lastOffset + 1,
                                entityType = entityType,
                                primaryId = primaryId.toLong()
                            )
                        }
                    }
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