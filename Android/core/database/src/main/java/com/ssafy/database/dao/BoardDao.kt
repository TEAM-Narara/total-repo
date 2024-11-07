package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.with.BoardInList
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
    suspend fun getLocalCreateBoards(): List<BoardInList>

    // 서버에 연산할 보드 조회
    @Query("""
        SELECT * 
        FROM board
        WHERE isStatus == 'UPDATE' OR isStatus == 'DELETE'
    """)
    suspend fun getLocalOperationBoards(): List<BoardEntity>

    // 보드 단일 조회
    @Query("SELECT * FROM board WHERE id = :boardId")
    fun getBoard(boardId: Long): BoardEntity

    // 보드 단일 조회
    @Query("SELECT * FROM board WHERE id = :boardId")
    fun getBoardFlow(boardId: Long): Flow<BoardEntity>

    // 워크스페이스에서 볼 것
    @Query("""
        SELECT * 
        FROM board 
        WHERE workspaceId == :workspaceId And isStatus != 'DELETE' And isClosed == 0
    """)
    fun getAllBoards(workspaceId: Long): Flow<List<BoardEntity>>

    // 아카이브에서 볼 것
    @Query("""
        SELECT * 
        FROM board 
        WHERE workspaceId == :workspaceId And isStatus != 'DELETE' And isClosed == 1
    """)
    fun getAllBoardsArchived(workspaceId: Long): Flow<List<BoardEntity>>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoards(boards: List<BoardEntity>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM board WHERE id NOT IN (:ids)")
    suspend fun deleteBoardsNotIn(ids: List<Long>)

    // 1. 휴지통 이동 (isArchive: false -> isArchive: true)
    // 2. 원격 삭제 (isArchive: true -> isStatus: 'DELETE')
    @Update
    suspend fun updateBoard(board: BoardEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteBoard(board: BoardEntity)
}

// STAY(원격) -> DELETE, UPDATE
// CREATE(로컬) -> UD 연산을 바로 해도 됨