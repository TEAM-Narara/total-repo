package com.ssafy.model.auth

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
