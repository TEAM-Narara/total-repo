package com.ssafy.datastore

import com.ssafy.model.user.User

interface DataStoreRepository {

    suspend fun saveUser(user: User)
    suspend fun getUser(): User
    suspend fun clearUser()

    suspend fun saveAccessToken(token: String)
    suspend fun getAccessToken(): String
    suspend fun clearAccessToken()

    suspend fun saveRefreshToken(token: String)
    suspend fun getRefreshToken(): String
    suspend fun clearRefreshToken()

    suspend fun clearAll()

    suspend fun getStompOffset(topic: String): Long
    suspend fun saveStompOffset(topic: String, offset: Long)
}
