package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.with.CardAllInfo
import com.ssafy.database.dto.with.CardWithListAndBoardName
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    // 카드 단일 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM card 
        WHERE id = :cardId AND isStatus != 'DELETE'
    """)
    fun getCard(cardId: Long): CardEntity?

    // 카드 단일 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM card 
        WHERE id = :cardId
    """)
    fun getCardFlow(cardId: Long): Flow<CardEntity?>

    // 오름차순
    @Transaction
    @Query("""
        SELECT * 
        FROM card
        WHERE listId = :listId 
            AND isStatus != 'DELETE'
            AND isArchived = 0
        ORDER BY myOrder ASC
        LIMIT 1
    """)
    fun getCardInListToTop(listId: Long): CardEntity?

    // 내림차순
    @Transaction
    @Query("""
        SELECT * 
        FROM card
        WHERE listId = :listId 
            AND isStatus != 'DELETE'
            AND isArchived = 0
        ORDER BY myOrder DESC
        LIMIT 1
    """)
    fun getCardInListToBottom(listId: Long): CardEntity?

    // 리스트 안에 카드들과 겹치는 MyOrder 존재 하는지 확인(충돌 방지)
    @Transaction
    @Query("""
        SELECT EXISTS(
            SELECT 1 
            FROM card
            WHERE listId = :listId 
                AND isStatus != 'DELETE'
                AND isArchived = 0
                AND myOrder = :myOrder
        )
    """)
    fun checkCardInListExistMyOrder(listId: Long, myOrder: Long): Boolean

    // 카드들의 리스트ID 조회
    @Query("""
        SELECT * 
        FROM card 
        WHERE id IN (:cardIds) 
            And isStatus != 'DELETE' 
            And isArchived = 0
    """)
    fun getCardsToList(cardIds: List<Long>): Flow<List<CardEntity>>

    // 리스트 내에 카드들 조회
    @Query("""
        SELECT * 
        FROM card 
        WHERE listId = :listId
            And isStatus != 'DELETE' 
            And isArchived = 0
        ORDER BY myOrder
    """)
    fun getAllCardsInList(listId: Long): List<CardEntity>

    // 리스트 내에 카드들 조회
    @Query("""
        SELECT * 
        FROM card 
        WHERE listId = :listId
            And isStatus != 'DELETE' 
            And isArchived = 0
        ORDER BY myOrder
    """)
    fun getAllCardsInListFlow(listId: Long): Flow<List<CardEntity>>

    // 리스트들 내에 카드들 조회
    @Query("""
        SELECT * 
        FROM card 
        WHERE listId IN (:listIds) 
            And isStatus != 'DELETE' 
            And isArchived = 0
        ORDER BY myOrder
    """)
    fun getAllCardsInLists(listIds: List<Long>): Flow<List<CardEntity>>

    // 리스트들 내에 카드들 조회
    @Query("""
        SELECT distinct c.*
            FROM card c
            LEFT JOIN card_member cm ON c.id = cm.cardId AND cm.isStatus != 'DELETE' AND isRepresentative = 1
            LEFT JOIN card_label cl ON c.id = cl.cardId AND cl.isStatus != 'DELETE'
            WHERE c.listId IN (:listIds)
              AND c.isStatus != 'DELETE'
              AND c.isArchived = 0
              -- 담당자
              AND (
                (:includeNoRepresentative = 0 AND :memberIdsEmpty = 1) OR
                    (
                        (:includeNoRepresentative = 1 AND cm.cardId IS NULL) OR
                        (cm.memberId IN (:memberIds))
                    )
              )
              -- 라벨
              AND (
                (:includeNoLabel = 0 AND :labelIdsEmpty = 1) OR
                    (
                        (:includeNoLabel = 1 AND cl.cardId IS NULL) OR
                        (cl.labelId IN (:labelIds))
                    )
              )
              -- 날짜
              AND (
                -- 아무 것도 선택하지 않은 경우, 모든 카드를 포함
                (
                    :noLimitDate = 0 AND
                    :expireDate = 0 AND
                    :deadlineDateType = 0
                ) OR
                
                -- 날짜 제한 없음이 선택된 경우, 모든 카드를 포함
                (:noLimitDate = 1) OR
                
                -- 날짜 제한 없음이 선택되지 않은 경우
                (
                    (:expireDate = 1 AND DATE(c.endAt / 1000, 'unixepoch', 'localtime') < DATE('now', 'localtime')) OR
                    (
                        (:deadlineDateType = 1 AND DATE(c.endAt / 1000, 'unixepoch', 'localtime') BETWEEN DATE('now', 'localtime') AND DATE('now', 'localtime', '+1 day')) OR
                        (:deadlineDateType = 2 AND DATE(c.endAt / 1000, 'unixepoch', 'localtime') BETWEEN DATE('now', 'localtime') AND DATE('now', 'localtime', '+7 day')) OR
                        (:deadlineDateType = 3 AND DATE(c.endAt / 1000, 'unixepoch', 'localtime') BETWEEN DATE('now', 'localtime') AND DATE('now', 'localtime', '+30 day'))
                    )
                )
            )
              AND (:keyword IS NULL OR c.name LIKE '%' || :keyword || '%')
            ORDER BY c.myOrder
    """)
    fun getAllCardsInListsFilter(
        listIds: List<Long>,
        includeNoRepresentative: Int,
        memberIdsEmpty: Int,
        memberIds: List<Long>,
        noLimitDate: Int,
        expireDate: Int,
        deadlineDateType: Int,
        includeNoLabel: Int,
        labelIdsEmpty: Int,
        labelIds: List<Long>,
        keyword: String?
    ): Flow<List<CardEntity>>

    // 아카이브에서 볼 것
    @Query("""
        SELECT * 
        FROM card 
        WHERE listId = :listId AND isStatus != 'DELETE' And isArchived = 1
        ORDER BY myOrder
    """)
    fun getAllCardsArchived(listId: Long): Flow<List<CardEntity>>

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
    fun getCardWithListAndBoardName(cardId: Long): Flow<CardWithListAndBoardName?>

    // 로컬에서 오프라인으로 생성한 카드 하위 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM card
        WHERE isStatus = 'CREATE'
    """)
    suspend fun getLocalCreateCard(): List<CardAllInfo>

    // 서버에 연산할 카드 조회
    @Query("""
        SELECT * 
        FROM card
        WHERE isStatus = 'UPDATE' OR isStatus = 'DELETE'
    """)
    suspend fun getLocalOperationCard(): List<CardEntity>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>): List<Long>

    // 1. 휴지통 이동 (isArchive: false -> isArchive: true)
    // 2. 원격 삭제 (isArchive: true -> isStatus: 'DELETE')
    @Update
    suspend fun updateCard(card: CardEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteCard(card: CardEntity)

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM card WHERE id NOT IN (:ids)")
    suspend fun deleteCardsNotIn(ids: List<Long>)

    @Query("DELETE FROM card WHERE id = :cardId")
    suspend fun deleteCardById(cardId: Long)
}