package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.with.CardMemberWithMemberInfo

@Dao
interface CardMemberDao {
    
    // 담당자 조회
    @Query(
        """
        SELECT *
        FROM card_member 
        WHERE cardId == :cardId AND isRepresentative == 1
    """
    )
    suspend fun getCardRepresentatives(cardId: Long): List<CardMemberWithMemberInfo>
    
    // 카드 멤버들 조회
    @Query("""
        SELECT *
        FROM card_member 
        WHERE cardId == :cardId
    """)
    suspend fun getCardMembers(cardId: Long): List<CardMemberWithMemberInfo>

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardMembers(cardMembers: List<CardMember>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM card_member WHERE id NOT IN (:ids)")
    suspend fun deleteCardMembersNotIn(ids: List<Long>)
}