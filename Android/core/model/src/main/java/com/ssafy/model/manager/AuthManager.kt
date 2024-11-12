package com.ssafy.model.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object AuthManager {
    const val NO_AUTH = "세션이 만료되었습니다. 다시 로그인해주세요."
    val noAuthEvent = MutableSharedFlow<Boolean>()

    fun sendNoAuthEvent() = CoroutineScope(Dispatchers.Main).launch {
        noAuthEvent.emit(true)
    }

}

object ConnectManager {
    const val CONNECTING = "서버와 동기화를 진행하고 있습니다..."
    val connectingEvent = MutableSharedFlow<Boolean>()

    fun sendConnectingEvent(isConnecting: Boolean) = CoroutineScope(Dispatchers.Main).launch {
        connectingEvent.emit(isConnecting)
    }

}
