package com.ssafy.ui.networkstate

import kotlinx.coroutines.flow.MutableStateFlow

object NetworkState {
    val isConnected = MutableStateFlow(false)
}
