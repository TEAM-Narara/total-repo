package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ssafy.database.dto.Alert
import com.ssafy.database.dto.MemberBackground

@Dao
interface AlertDao {
    @Query("SELECT * FROM alert")
    suspend fun getAllAlerts(): List<Alert>
}