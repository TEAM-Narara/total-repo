package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ssafy.database.dto.Board
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.BoardMemberAlarm
import com.ssafy.database.dto.Label
import com.ssafy.database.dto.with.BoardMemberWithMemberInfo

@Dao
interface BoardMemberDao {

    // 서버 보드 멤버 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM board_member
        WHERE isStatus != 'STAY'
    """)
    suspend fun getAllRemoteBoardMember(): List<BoardMember>

    // 서버 보드 멤버 알람 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM board_member_alarm
        WHERE isStatus != 'STAY'
    """)
    suspend fun getAllRemoteBoardMemberAlarm(): List<BoardMemberAlarm>

    // 보드 멤버 조회
    @Query("""
        SELECT *
        FROM board_member 
        WHERE boardId == :boardId
    """)
    suspend fun getBoardMembers(boardId: Long): List<BoardMemberWithMemberInfo>

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoardMembers(boardMembers: List<BoardMember>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM board_member WHERE id NOT IN (:ids)")
    suspend fun deleteBoardMembersNotIn(ids: List<Long>)
}