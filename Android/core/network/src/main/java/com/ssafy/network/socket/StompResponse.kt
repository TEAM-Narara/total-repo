package com.ssafy.network.socket

import com.google.gson.JsonObject

data class StompResponse(
    val target: String,
    val action: String,
    val data: JsonObject
)