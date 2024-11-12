package com.ssafy.data.repository.sync

interface SyncRepository {
    suspend fun syncAll()
}
