package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.with.CardAllInfo
import com.ssafy.database.dto.with.CardWithListAndBoardName
import kotlinx.coroutines.flow.Flow

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
    @Query("""
        SELECT * 
        FROM card
        WHERE isStatus == 'UPDATE' OR isStatus == 'DELETE'
    """)
    suspend fun getAllRemoteCard(): List<Card>

    // 카드 상세 조회
//    @Transaction
//    @Query("""
//        SELECT * 
//        FROM card 
//        WHERE id == :cardId
//    """)
//    suspend fun getCardDetail(cardId: Long): CardDetail
    
    // 카드 단일 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM card 
        WHERE id == :cardId
    """)
    fun getCard(cardId: Long): Flow<Card>
    
    // 카드 상위의 List, Board 이름 조회
    @Transaction
    @Query("""
        SELECT 
            card.id AS cardId,
            card.name AS cardName,
            list.name AS listName,
            board.name AS boardName
        FROM card
        INNER JOIN list ON list.id = card.listId
        INNER JOIN board ON board.id = list.boardId
        WHERE card.id = :cardId
    """)
    fun getCardWithListAndBoardName(cardId: Long): Flow<CardWithListAndBoardName>

    // 리스트 내에 카드들 조회
    @Query("""
        SELECT * 
        FROM card 
        WHERE listId == :listId
            And isStatus != 'DELETE' 
            And isArchived == 0
        ORDER BY myOrder
    """)
    fun getAllCardsInList(listId: Long): Flow<List<Card>>

    // 리스트들 내에 카드들 조회
    @Query("""
        SELECT * 
        FROM card 
        WHERE listId IN (:listIds) 
            And isStatus != 'DELETE' 
            And isArchived == 0
        ORDER BY myOrder
    """)
    fun getAllCardsInLists(listIds: List<Long>): Flow<List<Card>>

    // 아카이브에서 볼 것
    @Query("""
        SELECT * 
        FROM card 
        WHERE isStatus != 'DELETE' And isArchived == 1
        ORDER BY myOrder
    """)
    fun getAllCardsArchived(): Flow<List<Card>>

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