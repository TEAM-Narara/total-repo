package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.Label
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {

    // 로컬에서 오프라인으로 생성한 라벨 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM label
        WHERE isStatus == 'CREATE'
    """)
    suspend fun getAllLocalLabels(): List<Label>

    // 서버에 연산할 라벨 조회
    @Query("""
        SELECT * 
        FROM label
        WHERE isStatus == 'UPDATE' OR isStatus == 'DELETE'
    """)
    suspend fun getAllRemoteLabels(): List<Label>

    // 보드 라벨 단일 조회
    @Query("""
        SELECT * 
        FROM label 
        WHERE id == :id AND boardId == :boardId
    """)
    fun getLabel(id: Long, boardId: Long): Flow<Label>

    // 보드 라벨 모두 조회
    @Query("""
        SELECT * 
        FROM label 
        WHERE boardId == :boardId And isStatus != 'DELETE'
    """)
    fun getAllLabels(boardId: Long): Flow<List<Label>>

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabels(labels: List<Label>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM label WHERE id NOT IN (:ids)")
    suspend fun deleteLabelsNotIn(ids: List<Long>)

    // 원격 삭제 (isStatus: 'STAY' -> isStatus: 'DELETE')
    @Update
    suspend fun updateLabel(label: Label)
}