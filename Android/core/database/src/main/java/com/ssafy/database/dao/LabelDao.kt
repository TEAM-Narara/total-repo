package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.CardLabel
import com.ssafy.database.dto.Label
import com.ssafy.database.dto.with.CardAllInfo

@Dao
interface LabelDao {

    // 로컬 라벨 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM label
        WHERE isStatus == 'CREATE'
    """)
    suspend fun getAllLocalLabel(): List<Label>

    // 서버 라벨 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM label
        WHERE isStatus != 'CREATE' AND isStatus != 'STAY'
    """)
    suspend fun getAllRemoteLabel(): List<Label>

    // 보드 라벨 모두 조회
    @Query("""
        SELECT * 
        FROM label 
        WHERE boardId == :boardId
    """)
    suspend fun getAllLabels(boardId: Long): List<Label>

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