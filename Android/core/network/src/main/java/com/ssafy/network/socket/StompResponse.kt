package com.ssafy.network.socket

data class StompResponse(
    val offset: Long,
    val updateAt: Long,
    val target: String,
    val action: String,
    val data: String
)