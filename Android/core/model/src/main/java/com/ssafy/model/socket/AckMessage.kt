package com.ssafy.model.socket

data class AckMessage(
    val offset: Long,
    val topic: String,
    val partition: Long,
    val groupId: String
)