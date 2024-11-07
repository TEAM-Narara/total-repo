package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.with.ListInCards
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {

    // 로컬에서 오프라인으로 생성한 리스트 하위 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM list
        WHERE isStatus == 'CREATE'
    """)
    suspend fun getLocalCreateList(): List<ListInCards>

    // 서버에 연산할 리스트 조회
    @Query("""
        SELECT * 
        FROM list
        WHERE isStatus == 'UPDATE' OR isStatus == 'DELETE'
    """)
    suspend fun getLocalOperationList(): List<ListEntity>

    // 리스트 단일 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM list 
        WHERE id == :listId And isStatus != 'DELETE' And isArchived == 0
    """)
    fun getList(listId: Long): ListEntity

    // 현재 보드에서 볼 것
    @Transaction
    @Query("""
        SELECT * 
        FROM list
        WHERE boardId = :boardId 
            AND isStatus != 'DELETE' 
            AND isArchived = 0
        ORDER BY myOrder
    """)
    fun getAllListsInBoard(boardId: Long): Flow<List<ListEntity>>

    // 아카이브에서 볼 것
    @Transaction
    @Query("""
        SELECT * 
        FROM list 
        WHERE isStatus != 'DELETE' 
            And isArchived == 1
            And boardId == :boardId
        ORDER BY myOrder
    """)
    fun getAllListsArchived(boardId: Long): Flow<List<ListEntity>>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ListEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLists(lists: List<ListEntity>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM list WHERE id NOT IN (:ids)")
    suspend fun deleteListsNotIn(ids: List<Long>)

    // 1. 휴지통 이동 (isArchive: false -> isArchive: true)
    // 2. 원격 삭제 (isArchive: true -> isStatus: 'DELETE')
    @Update
    suspend fun updateList(list: ListEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteList(list: ListEntity)
}