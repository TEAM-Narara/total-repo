package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.BoardMemberAlarm
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.CardMemberAlarm
import com.ssafy.database.dto.with.CardMemberWithMemberInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface CardMemberDao {

    // 서버에 연산할 카드 멤버 조회
    @Query("""
        SELECT * 
        FROM card_member
        WHERE isStatus != 'STAY'
    """)
    suspend fun getAllRemoteCardMember(): List<CardMember>

    // 서버에 연산할 카드 멤버 알람 조회
    @Query("""
        SELECT * 
        FROM card_member_alarm
        WHERE isStatus != 'STAY'
    """)
    suspend fun getAllRemoteCardMemberAlarm(): List<CardMemberAlarm>
    
    // 담당자 조회
    @Transaction
    @Query(
        """
        SELECT *
        FROM card_member 
        WHERE cardId == :cardId AND isRepresentative == 1
    """
    )
    fun getCardRepresentatives(cardId: Long): Flow<List<CardMemberWithMemberInfo>>
    
    // 카드 멤버들 조회
    @Transaction
    @Query("""
        SELECT *
        FROM card_member 
        WHERE cardId == :cardId
    """)
    fun getCardMembers(cardId: Long): Flow<List<CardMemberWithMemberInfo>>

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardMembers(cardMembers: List<CardMember>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM card_member WHERE id NOT IN (:ids)")
    suspend fun deleteCardMembersNotIn(ids: List<Long>)
}