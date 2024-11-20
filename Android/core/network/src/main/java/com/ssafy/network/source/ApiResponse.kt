package com.ssafy.network.source

data class ApiResponse<T>(
    val statusCode: Int,
    val responseMessage: String,
    val data : T
)
