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
import com.ssafy.network.networkstate.NetworkState
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
                        }

                        "FETCHED" -> {
                            stompClientManager.send(
                                SOCKET_ID, ACK_LAST_URL, AckMessage(
                                    offset = response.offset,
                                    topic = topic.split("/").joinToString("-"),
                                    partition = response.partition,
                                    groupId = "member-$memberId"
                                )
                            )
                        }

                        else -> return
                    }

                    dataStoreRepository.saveStompOffset(topic, response.offset)
                }

                override suspend fun onDataReleased(response: StompResponse) {
                    when (response.type) {
                        "RECEIVED" -> emit(gson.fromJson(response.data, StompMessage::class.java))
                        "FETCHED" -> {
                            gson.fromJson<List<StompFetchMessage>>(
                                response.data,
                                object : TypeToken<List<StompFetchMessage>>() {}.type
                            ).forEach {
                                emit(it.message)
                            }
                        }
                    }
                }

                override fun onTimeout(lastOffset: Long) {
                    val (entityType, primaryId) = topic.split("/")
                    CoroutineScope(Dispatchers.IO).launch {
                        runCatching {
                            val user = dataStoreRepository.getUser()
                            if (user.memberId == 0L && user.email.isEmpty() && user.nickname.isEmpty()) return@runCatching
                            kafkaAPI.sync(
                                partition = 0,
                                offset = lastOffset + 1,
                                entityType = entityType,
                                primaryId = primaryId.toLong()
                            )
                        }
                    }
                }

                override fun superPass(response: StompResponse): Boolean {
                    return response.type == "FETCHED"
                }
            }
        )

        state.collect {
            when (it) {
                ConnectionState.Connected -> stompClientManager.subscribe(
                    SOCKET_ID,
                    "/topic/$topic/member/$memberId",
                ).collect(dataHandler::handleSocketData)

                else -> {}
            }
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            NetworkState.isConnected.collect {
                if (it) {
                    stompClientManager.connect(SOCKET_ID, SOCKET_URL) {
                        Log.i("TAG", "subscribe: Socket is connected")
                        ConnectManager.sendConnectingEvent(true)
                        syncRepository.syncAll()
                        Log.i("TAG", "subscribe: Sync all")
                        ConnectManager.sendConnectingEvent(false)
                    }
                } else {
                    stompClientManager.disconnect(SOCKET_ID)
                }
            }
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