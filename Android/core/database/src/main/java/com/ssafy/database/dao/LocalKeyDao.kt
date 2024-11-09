package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ssafy.database.dto.piece.LocalKeyEntity
import com.ssafy.database.dto.piece.LocalTable
import kotlinx.coroutines.sync.Mutex

@Dao
interface LocalKeyDao {
    @Query("SELECT pk FROM local_key WHERE id = :tableName")
    suspend fun getLastNegativeId(tableName: LocalTable): Long?

    @Upsert
    suspend fun upsertId(localKey: LocalKeyEntity)

    @Transaction
    suspend fun getAutoDecrementTableId(tableName: LocalTable): Long {
        val lastId = getLastNegativeId(tableName) ?: 0L
        val newId = if (lastId == 0L) -1L else lastId - 1
        upsertId(LocalKeyEntity(tableName, newId))
        return newId
    }
}