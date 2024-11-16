package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.CardMemberAlarmEntity
import com.ssafy.database.dto.CardMemberEntity
import com.ssafy.database.dto.with.CardMemberWithMemberInfo
import com.ssafy.database.dto.with.MemberWithRepresentative
import com.ssafy.model.board.MemberResponseDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface CardMemberDao {

    @Query("SELECT * FROM card_member WHERE cardId = :cardId AND memberId = :memberId")
    fun getCardMember(cardId: Long, memberId: Long): CardMemberEntity?

    // 카드 멤버 단일 조회
    @Query("SELECT * FROM card_member WHERE cardId = :cardId AND memberId = :memberId")
    fun getCardMemberFlow(cardId: Long, memberId: Long): Flow<CardMemberEntity?>

    // 내가 담당자인 카드 조회
    @Query("""
       SELECT cardId
        FROM card_member 
        WHERE isRepresentative = 1 AND memberId = :memberId AND isStatus != 'DELETE'
    """)
    fun getRepresentativeCardMember(memberId: Long): Flow<List<Long>>

    // 카드 멤버들 조회
    @Transaction
    @Query("""
        SELECT 
            card_member.id AS card_member_id,
            card_member.memberId AS card_member_memberId,
            card_member.cardId AS card_member_cardId,
            card_member.isRepresentative AS card_member_isRepresentative,
            card_member.isStatus AS card_member_isStatus,
            member.id AS member_id,
            member.email AS member_email,
            member.nickname AS member_nickname,
            member.profileImageUrl AS member_profileImageUrl
        FROM card_member 
        INNER JOIN member ON member.id = card_member.memberId
        WHERE card_member.cardId = :cardId AND card_member.isStatus != 'DELETE'
    """)
    fun getCardMembers(cardId: Long): Flow<List<CardMemberWithMemberInfo>>

    // 카드의 담당자들 조회
    @Transaction
    @Query(
        """
        SELECT 
            card_member.id AS card_member_id,
            card_member.memberId AS card_member_memberId,
            card_member.cardId AS card_member_cardId,
            card_member.isRepresentative AS card_member_isRepresentative,
            card_member.isStatus AS card_member_isStatus,
            member.id AS member_id,
            member.email AS member_email,
            member.nickname AS member_nickname,
            member.profileImageUrl AS member_profileImageUrl
        FROM card_member 
        INNER JOIN member ON member.id = card_member.memberId
        WHERE card_member.cardId = :cardId AND card_member.isStatus != 'DELETE' AND isRepresentative = 1
    """)
    fun getCardRepresentativesInCard(cardId: Long): Flow<List<CardMemberWithMemberInfo>>

    // 카드들의 담당자들 조회
    @Transaction
    @Query(
        """
        SELECT 
            card_member.id AS card_member_id,
            card_member.memberId AS card_member_memberId,
            card_member.cardId AS card_member_cardId,
            card_member.isRepresentative AS card_member_isRepresentative,
            card_member.isStatus AS card_member_isStatus,
            member.id AS member_id,
            member.email AS member_email,
            member.nickname AS member_nickname,
            member.profileImageUrl AS member_profileImageUrl
        FROM card_member 
        INNER JOIN member ON member.id = card_member.memberId
        WHERE card_member.cardId IN (:cardIds) AND card_member.isStatus != 'DELETE' AND isRepresentative = 1
    """)
    fun getCardRepresentativesInCards(cardIds: List<Long>): Flow<List<CardMemberWithMemberInfo>>

    @Transaction
    @Query("""
        SELECT 
            m.id AS memberId,
            m.email AS email,
            m.nickname AS nickname,
            m.profileImageUrl AS profileImageUrl,
            CASE 
                WHEN EXISTS (
                    SELECT 1 FROM card_member cm 
                    WHERE cm.memberId = m.id AND cm.isRepresentative = 1 AND cm.isStatus != 'DELETE'
                ) THEN 1 
                ELSE 0 
            END AS isRepresentative
        FROM member m
        WHERE m.id IN (
            SELECT memberId FROM workspace_member WHERE workspaceId = :workspaceId AND isStatus != 'DELETE'
            UNION
            SELECT memberId FROM board_member WHERE boardId = :boardId AND isStatus != 'DELETE'
            UNION
            SELECT memberId FROM card_member WHERE cardId = :cardId AND isStatus != 'DELETE'
        )
    """)
    fun getMembersWithRepresentativeFlag(
        workspaceId: Long,
        boardId: Long,
        cardId: Long
    ): Flow<List<MemberWithRepresentative>>

    // 서버에 연산할 카드 멤버 조회
    @Query("""
        SELECT * 
        FROM card_member
        WHERE isStatus != 'STAY'
    """)
    suspend fun getLocalOperationCardMember(): List<CardMemberEntity>

    // 단일 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardMember(cardMember: CardMemberEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardMembers(cardMembers: List<CardMemberEntity>): List<Long>

    // 상태 업데이트
    @Update
    suspend fun updateCardMember(cardMember: CardMemberEntity)

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM card_member WHERE id NOT IN (:ids)")
    suspend fun deleteCardMembersNotIn(ids: List<Long>)


    // 카드 멤버 알람 단일 조회
    @Query("SELECT * FROM card_member_alarm WHERE cardId = :cardId")
    fun getCardMemberAlarm(cardId: Long): CardMemberAlarmEntity?

    // 카드 멤버 알람 단일 조회
    @Query("SELECT * FROM card_member_alarm WHERE cardId = :cardId")
    fun getCardMemberAlarmFlow(cardId: Long): Flow<CardMemberAlarmEntity?>

    // 카드 멤버 알람 조회
    @Query("""
        SELECT * 
        FROM card_member_alarm
        WHERE cardId IN (:cardIds) 
    """)
    fun getCardsMemberAlarms(cardIds: List<Long>): Flow<List<CardMemberAlarmEntity>>

    // 서버에 연산할 카드 멤버 알람 조회
    @Query("""
        SELECT * 
        FROM card_member_alarm
        WHERE isStatus != 'STAY'
    """)
    suspend fun getLocalOperationCardMemberAlarm(): List<CardMemberAlarmEntity>

    // 카드 알람 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardAlarm(cardMemberAlarm: CardMemberAlarmEntity): Long

    // 알람 상태 업데이트
    @Update
    suspend fun updateCardMemberAlarm(cardMemberAlarm: CardMemberAlarmEntity)

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM card_member WHERE id = :cardMemberId")
    suspend fun deleteCardMemberById(cardMemberId: Long)
}