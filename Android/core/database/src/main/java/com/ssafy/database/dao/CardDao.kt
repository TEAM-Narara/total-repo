package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.SbList
import com.ssafy.database.dto.with.CardAllInfo
import com.ssafy.database.dto.with.CardDetail
import com.ssafy.database.dto.with.ListInCards

@Dao
interface CardDao {

    // 로컬에서 오프라인으로 생성한 카드 하위 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM card
        WHERE isStatus == 'CREATE'
    """)
    suspend fun getAllLocalCard(): List<CardAllInfo>

    // 서버에 연산할 카드 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM card
        WHERE isStatus == 'UPDATE' OR isStatus == 'DELETE'
    """)
    suspend fun getAllRemoteCard(): List<Card>

    // 카드 상세 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM card 
        WHERE id == :cardId
    """)
    suspend fun getCardDetail(cardId: Long): CardDetail

    // 워크스페이스에서 볼 것
    @Query("""
        SELECT * 
        FROM card 
        WHERE listId == :listId And isStatus != 'DELETE' And isArchived == 0
    """)
    suspend fun getAllCards(listId: Long): List<Card>

    // 아카이브에서 볼 것
    @Query("""
        SELECT * 
        FROM card 
        WHERE isStatus != 'DELETE' And isArchived == 1
    """)
    suspend fun getAllCardsArchived(): List<Card>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<Card>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM card WHERE id NOT IN (:ids)")
    suspend fun deleteCardsNotIn(ids: List<Long>)

    // 1. 휴지통 이동 (isArchive: false -> isArchive: true)
    // 2. 원격 삭제 (isArchive: true -> isStatus: 'DELETE')
    @Update
    suspend fun updateCard(card: Card)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteCard(card: Card)
}