package com.ssafy.ui

import kotlinx.coroutines.flow.MutableStateFlow

object NetworkState {
    val isConnected = MutableStateFlow(false)
}
