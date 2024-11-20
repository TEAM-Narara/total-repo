package com.ssafy.data.repository.clear

import com.ssafy.database.database.SBDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearRoomRepositoryImpl @Inject constructor(
    private val sbDatabase: SBDatabase
) : ClearRoomRepository {

    override suspend fun clearAll() {
        sbDatabase.clearAllTables()
    }

}