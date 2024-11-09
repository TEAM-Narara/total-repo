package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ssafy.database.dto.AlertEntity
import com.ssafy.database.dto.with.AlertWithMemberInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    // 알람 멤버 조회
    @Transaction
    @Query("""
        SELECT 
            alert.id AS alert_id,
            alert.memberId AS alert_memberId,
            alert.title AS alert_title,
            alert.body AS alert_body,
            alert.createAt AS alert_createAt,
            alert.updateAt AS alert_updateAt,
            member.id AS member_id,
            member.email AS member_email,
            member.nickname AS member_nickname,
            member.profileImageUrl AS member_profileImageUrl
        FROM alert
        INNER JOIN member ON member.id = alert.memberId
    """)
    fun getAllAlertsWithMemberInfo(): Flow<List<AlertWithMemberInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(attachments: AlertEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(attachments: List<AlertEntity>): List<Long>
}