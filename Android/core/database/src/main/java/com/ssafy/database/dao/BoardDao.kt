package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ssafy.database.dto.Board

@Dao
interface BoardDao {
    // 화면에서 볼 것
    @Query("SELECT * FROM board WHERE isStatus != 'DELETE' And isClosed == 0")
    suspend fun getAllBoards(): List<Board>

    // 아카이브에서 볼 것
    @Query("SELECT * FROM board WHERE isStatus != 'DELETE' And isClosed == 1")
    suspend fun getAllBoardsArchived(): List<Board>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: Board): Long

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