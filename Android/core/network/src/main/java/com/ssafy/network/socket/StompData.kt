package com.ssafy.network.socket

import com.google.gson.JsonObject

data class StompData(
    val target: String,
    val action: String,
    val data: JsonObject
)

data class StompResponse<T>(
    val offset: Long,
    val partition: Long,
    val data: T,
)
