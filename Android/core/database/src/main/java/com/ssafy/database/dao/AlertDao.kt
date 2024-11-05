package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.database.dto.Alert
import com.ssafy.database.dto.Attachment
import com.ssafy.database.dto.MemberBackground
import com.ssafy.database.dto.with.AlertWithMemberInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    // 알람 멤버 조회
    @Query("""
        SELECT alert.*, member.*
        FROM alert
        INNER JOIN member ON member.id = alert.memberId
    """)
    fun getAllAlertsWithMemberInfo(): Flow<List<AlertWithMemberInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(attachments: List<Alert>): List<Long>
}