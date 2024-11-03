package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.database.dto.Alert
import com.ssafy.database.dto.Attachment
import com.ssafy.database.dto.MemberBackground
import com.ssafy.database.dto.with.AlertWithMemberInfo

@Dao
interface AlertDao {
    @Query("SELECT * FROM alert")
    suspend fun getAllAlerts(): List<Alert>

    @Query("""
        SELECT 
            alert.id AS alertId, 
            alert.memberId AS memberId, 
            alert.title AS title, 
            alert.body AS body, 
            alert.status AS status, 
            member.nickname AS nickname, 
            member.profileImageUrl AS profileImageUrl 
        FROM alert 
        INNER JOIN member ON alert.memberId = member.id
    """)
    suspend fun getAllAlertsWithMemberInfo(): List<AlertWithMemberInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(attachments: List<Alert>): List<Long>
}