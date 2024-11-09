package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.with.BoardMemberWithMemberInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardMemberDao {

    // 보드 멤버 단일 id 조회
    @Query("SELECT * FROM board_member WHERE id == :id")
    fun getBoardMember(id: Long): BoardMemberEntity?

    @Query("SELECT * FROM board_member WHERE boardId = :boardId AND memberId = :memberId")
    fun getBoardMember(boardId: Long, memberId: Long): BoardMemberEntity?

    // 보드 멤버 단일 조회
    @Query("SELECT * FROM board_member WHERE boardId = :boardId AND memberId = :memberId")
    fun getBoardMemberFlow(boardId: Long, memberId: Long): Flow<BoardMemberEntity?>

    // 보드 멤버 상세 조회
    @Transaction
    @Query("""
        SELECT 
            board_member.id AS board_member_id,
            board_member.memberId AS board_member_memberId,
            board_member.boardId AS board_member_boardId,
            board_member.authority AS board_member_authority,
            board_member.isStatus AS board_member_isStatus,
            member.id AS member_id,
            member.email AS member_email,
            member.nickname AS member_nickname,
            member.profileImageUrl AS member_profileImageUrl
        FROM board_member 
        INNER JOIN member ON member.id = board_member.memberId
        WHERE board_member.boardId = :boardId AND board_member.isStatus != 'DELETE'
    """)
    fun getBoardMembers(boardId: Long): Flow<List<BoardMemberWithMemberInfo>>

    // 서버에 연산할 보드 멤버 조회
    @Query("""
        SELECT * 
        FROM board_member
        WHERE isStatus != 'STAY'
    """)
    suspend fun getLocalOperationBoardMember(): List<BoardMemberEntity>

    // 단일 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoardMember(boardMember: BoardMemberEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoardMembers(boardMembers: List<BoardMemberEntity>): List<Long>

    // 상태 업데이트
    @Update
    suspend fun updateBoardMember(boardMember: BoardMemberEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteBoardMember(boardMember: BoardMemberEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Query("""
        DELETE FROM board_member 
        WHERE memberId = :memberId AND boardId = :boardId
    """)
    suspend fun deleteLocalBoardMember(boardId: Long, memberId: Long)

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM board_member WHERE id NOT IN (:ids)")
    suspend fun deleteBoardMembersNotIn(ids: List<Long>)


    // 보드 알람 조회
    @Query("""
        SELECT *
        FROM board_member_alarm 
        WHERE boardId = :boardId
    """)
    fun getBoardMemberAlarm(boardId: Long): BoardMemberAlarmEntity?

    // 보드 알람 조회
    @Query("""
        SELECT *
        FROM board_member_alarm 
        WHERE boardId = :boardId
    """)
    fun getBoardMemberAlarmFlow(boardId: Long): Flow<BoardMemberAlarmEntity?>

    // 서버에 연산할 내 보드별 알람 조회
    @Query("""
        SELECT * 
        FROM board_member_alarm
        WHERE isStatus != 'STAY'
    """)
    suspend fun getLocalOperationBoardMemberAlarm(): List<BoardMemberAlarmEntity>

    // 보드 알람 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoardAlarm(boardMemberAlarm: BoardMemberAlarmEntity): Long

    // 알람 상태 업데이트
    @Update
    suspend fun updateBoardMemberAlarm(boardMemberAlarm: BoardMemberAlarmEntity)
}