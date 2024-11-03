package com.ssafy.model.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object AuthManager {
    val noAuthEvent = MutableSharedFlow<Unit>()

    fun sendNoAuthEvent() = CoroutineScope(Dispatchers.Main).launch {
        noAuthEvent.emit(Unit)
    }

}
