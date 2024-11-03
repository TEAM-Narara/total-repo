package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.SbList
import com.ssafy.database.dto.with.CardThumbnail
import com.ssafy.database.dto.with.ListInCards

@Dao
interface ListDao {

    // 리스트 단일 조회
    @Query("""
        SELECT * 
        FROM list 
        WHERE id == :listId And isStatus != 'DELETE' And isArchived == 0
    """)
    suspend fun getList(listId: Long): List<ListInCards>

    // 현재 보드에서 볼 것
    @Query("""
        SELECT * 
        FROM list 
        WHERE boardId == :boardId And isStatus != 'DELETE' And isArchived == 0
    """)
    suspend fun getAllLists(boardId: Long): List<ListInCards>

    // 아카이브에서 볼 것
    @Query("""
        SELECT * 
        FROM list 
        WHERE isStatus != 'DELETE' And isArchived == 1
    """)
    suspend fun getAllListsArchived(): List<ListInCards>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: SbList): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLists(lists: List<SbList>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM list WHERE id NOT IN (:ids)")
    suspend fun deleteListsNotIn(ids: List<Long>)

    // 1. 휴지통 이동 (isArchive: false -> isArchive: true)
    // 2. 원격 삭제 (isArchive: true -> isStatus: 'DELETE')
    @Update
    suspend fun updateList(list: SbList)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteList(list: SbList)
}