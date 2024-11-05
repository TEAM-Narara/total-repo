package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.CardLabel
import com.ssafy.database.dto.with.CardLabelWithLabelInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface CardLabelDao {

    // 로컬에서 오프라인으로 생성한 카드라벨 조회
    @Query("""
        SELECT * 
        FROM card_label
        WHERE isStatus == 'CREATE'
    """)
    suspend fun getAllLocalCardLabels(): List<CardLabel>

    // 서버에 연산할 라벨 조회
    @Query("""
        SELECT * 
        FROM card_label
        WHERE isStatus == 'UPDATE' OR isStatus == 'DELETE'
    """)
    suspend fun getAllRemoteCardLabels(): List<CardLabel>

    // 카드 라벨 모두 조회
    @Transaction
    @Query("""
        SELECT 
            card_label.id AS card_label_id,
            card_label.labelId AS card_label_labelId,
            card_label.cardId AS card_label_cardId,
            card_label.isActivated AS card_label_isActivated,
            card_label.isStatus AS card_label_isStatus,
            label.id AS label_id,
            label.boardId AS label_boardId,
            label.name AS label_name,
            label.color AS label_color,
            label.isStatus AS label_isStatus
        FROM card_label
        INNER JOIN label ON label.id = card_label.labelId
        WHERE card_label.cardId = :cardId AND card_label.isStatus != 'DELETE'
    """)
    fun getAllCardLabels(cardId: Long): Flow<List<CardLabelWithLabelInfo>>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardLabel(cardLabel: CardLabel): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardLabels(cardLabels: List<CardLabel>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM card_label WHERE id NOT IN (:ids)")
    suspend fun deleteCardLabelsNotIn(ids: List<Long>)

    // 원격 삭제 (isStatus: 'STAY' -> isStatus: 'DELETE')
    @Update
    suspend fun updateCardLabel(cardLabel: CardLabel)
}