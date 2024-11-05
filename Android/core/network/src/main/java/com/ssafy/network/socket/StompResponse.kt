package com.ssafy.network.socket

data class StompResponse<T>(
    val target: String = "",
    val action: String = "",
    val data: T? = null
)