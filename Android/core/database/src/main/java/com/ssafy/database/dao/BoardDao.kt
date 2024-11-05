package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.ssafy.database.dto.Board
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.BoardMemberAlarm
import com.ssafy.database.dto.Workspace
import com.ssafy.database.dto.with.BoardDetail
import com.ssafy.database.dto.with.BoardInList
import com.ssafy.database.dto.with.BoardMemberWithMemberInfo
import com.ssafy.database.dto.with.WorkspaceInBoard
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {

    // 로컬에서 오프라인으로 생성한 보드 하위 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM board
        WHERE isStatus == 'CREATE'
    """)
    suspend fun getAllLocalBoards(): List<BoardInList>

    // 서버에 연산할 보드 조회
    @Query("""
        SELECT * 
        FROM board
        WHERE isStatus == 'UPDATE' OR isStatus == 'DELETE'
    """)
    suspend fun getAllRemoteBoards(): List<Board>

    // 보드 단일 조회
    @Query("SELECT * FROM board WHERE id = :boardId")
    fun getBoard(boardId: Long): Flow<Board>

    // 워크스페이스에서 볼 것
    @Query("""
        SELECT * 
        FROM board 
        WHERE workspaceId == :workspaceId And isStatus != 'DELETE' And isClosed == 0
    """)
    fun getAllBoards(workspaceId: Long): Flow<List<Board>>

    // 아카이브에서 볼 것
    @Query("""
        SELECT * 
        FROM board 
        WHERE isStatus != 'DELETE' And isClosed == 1
    """)
    fun getAllBoardsArchived(): Flow<List<Board>>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: Board): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoards(boards: List<Board>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM board WHERE id NOT IN (:ids)")
    suspend fun deleteBoardsNotIn(ids: List<Long>)

    // 1. 휴지통 이동 (isArchive: false -> isArchive: true)
    // 2. 원격 삭제 (isArchive: true -> isStatus: 'DELETE')
    @Update
    suspend fun updateBoard(board: Board)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteBoard(board: Board)
}

// STAY(원격) -> DELETE, UPDATE
// CREATE(로컬) -> UD 연산을 바로 해도 됨