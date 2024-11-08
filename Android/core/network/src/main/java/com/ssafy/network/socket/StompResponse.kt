package com.ssafy.network.socket

import com.google.gson.JsonObject

data class StompResponse(
    val offset: Long,
    val updateAt: Long,
    val target: String,
    val action: String,
    val data: JsonObject
)