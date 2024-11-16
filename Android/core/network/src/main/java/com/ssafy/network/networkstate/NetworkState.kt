package com.ssafy.network.networkstate

import kotlinx.coroutines.flow.MutableStateFlow

object NetworkState {
    val isConnected = MutableStateFlow(false)
}
