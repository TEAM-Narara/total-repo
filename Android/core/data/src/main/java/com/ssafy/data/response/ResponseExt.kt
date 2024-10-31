package com.ssafy.data.response

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

const val ERROR = "에러 발생"

inline fun <reified T> Response<T>.toFlow(): Flow<T> = flow {

    body()?.let {
        if (isSuccessful) return@flow emit(it)
        else throw RuntimeException(ERROR)
    }

    errorBody()?.let {
        val errorMessage = it.charStream().readLines().joinToString()
        if (errorMessage.isNotBlank()) throw RuntimeException(errorMessage)
    }

    if (isSuccessful) {
        if (T::class == Unit::class) return@flow emit(Unit as T)
        else throw RuntimeException(ERROR)
    } else throw RuntimeException(ERROR)
}
