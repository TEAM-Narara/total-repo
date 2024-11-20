package com.ssafy.database.dao

import com.ssafy.database.dto.piece.LocalTable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NegativeIdGenerator @Inject constructor(
    private val localKeyDao: LocalKeyDao
) {
    private val mutex = Mutex()

    suspend fun getNextNegativeId(tableName: LocalTable): Long {
        return mutex.withLock {
            localKeyDao.getAutoDecrementTableId(tableName)
        }
    }
}