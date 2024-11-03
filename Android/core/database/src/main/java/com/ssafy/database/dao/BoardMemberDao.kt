package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.database.dto.Board
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.with.BoardMemberWithMemberInfo

@Dao
interface BoardMemberDao {
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