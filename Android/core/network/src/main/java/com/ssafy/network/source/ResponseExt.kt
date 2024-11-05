package com.ssafy.network.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response

const val ERROR = "에러 발생"

inline fun <reified T> Response<ApiResponse<T>>.toFlow(): Flow<T> = flow {

    body()?.let {
        if (isSuccessful) return@flow emit(it.data)
        else throw RuntimeException(ERROR)
    }

    errorBody()?.let {
        val errorMessage = it.charStream().readLines().joinToString()
        val json = JSONObject(errorMessage)
        val message = json.getString("responseMessage")

        if (message.isNotBlank()) throw RuntimeException(message)
        else throw RuntimeException(ERROR)
    }

    if (isSuccessful) {
        if (T::class == Unit::class) return@flow emit(Unit as T)
        else throw RuntimeException(ERROR)
    } else throw RuntimeException(ERROR)
}
