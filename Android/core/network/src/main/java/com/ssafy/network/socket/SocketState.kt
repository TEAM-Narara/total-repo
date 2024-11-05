package com.ssafy.network.socket

sealed class ConnectionState {
    data object Connected : ConnectionState()
    data object Connecting : ConnectionState()
    data object Disconnected : ConnectionState()
    data class Error(val throwable: Throwable) : ConnectionState()
}