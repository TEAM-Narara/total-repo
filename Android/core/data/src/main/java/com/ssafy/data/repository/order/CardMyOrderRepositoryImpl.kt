package com.ssafy.data.repository.order

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.order.MoveConst.DEFAULT_TOP_ORDER
import com.ssafy.data.repository.order.MoveConst.HALF_DIVIDER
import com.ssafy.data.repository.order.MoveConst.LARGE_INCREMENT
import com.ssafy.data.repository.order.MoveConst.MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
import com.ssafy.data.repository.order.MoveConst.MOVE_BOTTOM_ORDER_RATIO
import com.ssafy.data.repository.order.MoveConst.MOVE_TOP_ORDER_RATIO
import com.ssafy.data.repository.order.MoveConst.REORDER_GAP
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.ListEntity
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong
import kotlin.random.Random

@Singleton
class CardMyOrderRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
    private val listDao: ListDao,
) : CardMyOrderRepository {
    override suspend fun moveCardToTop(
        cardId: Long,
        targetListId: Long,
        isConnection: Boolean
    ): CardMoveResult? {
        // 이동할 카드 조회
        var targetCard = cardDao.getCard(cardId) ?: return null

        // targetListId가 0L이면 현재 카드의 리스트를 사용
        val targetList = if (targetListId == 0L) {
            listDao.getList(targetCard.listId)
        } else {
            listDao.getList(targetListId)
        } ?: return null

        // 최상위 카드 조회
        val topCard = cardDao.getCardInListToTop(targetList.id)

        return if (topCard == null) {
            // 보드에 리스트가 없는 경우

            CardMoveResult.ReorderedCardMove(
                listOf(
                    CardMoveResponseDto(
                        listId = targetListId,
                        cardId = targetCard.id,
                        myOrder = DEFAULT_TOP_ORDER
                    )
                )
            )
        } else if (topCard.myOrder < 1) {
            // 의도적으로 재시도 로직 실행
            val orderInfoList = generateUniqueOrderWithRetry(
                targetCard = targetCard,
                targetIndex = 0,
                targetList = targetList,
                baseOrder = -1000L,
                prevOrder = null,
                nextOrder = topCard.myOrder
            )
            CardMoveResult.ReorderedCardMove(orderInfoList)
        } else if (targetCard.myOrder == topCard.myOrder && targetList.id == targetCard.listId) {
            // 이미 최상단에 위치한 경우
            CardMoveResult.ReorderedCardMove(
                listOf(
                    CardMoveResponseDto(
                        listId = targetList.id,
                        cardId = targetCard.id,
                        myOrder = targetCard.myOrder
                    )
                )
            )
        } else {
            // 기준 순서 값 계산
            val baseOrder = max(
                topCard.myOrder - LARGE_INCREMENT,
                (topCard.myOrder * MOVE_TOP_ORDER_RATIO).roundToLong()
            )

            // 고유 순서 값 생성 및 재배치 체크
            val orderInfoList = generateUniqueOrderWithRetry(
                targetCard = targetCard,
                targetIndex = 0,
                targetList = targetList,
                baseOrder = baseOrder,
                prevOrder = null,
                nextOrder = topCard.myOrder
            )

            if (orderInfoList.size > 1) {
                // 재배치 필요 - 전체 리스트 반환
                CardMoveResult.ReorderedCardMove(orderInfoList)
            } else {
                // 단일 카드 이동
                val newOrder = orderInfoList.first().myOrder

                CardMoveResult.ReorderedCardMove(
                    listOf(
                        CardMoveResponseDto(
                            listId = targetListId,
                            cardId = targetCard.id,
                            myOrder = newOrder
                        )
                    )
                )
            }
        }
    }

    override suspend fun moveCardToBottom(
        cardId: Long,
        targetListId: Long,
        isConnection: Boolean
    ): CardMoveResult? {

        // 이동할 카드 조회
        var targetCard = cardDao.getCard(cardId) ?: return null

        // targetListId가 0L이면 현재 카드의 리스트를 사용
        val targetList = if (targetListId == 0L) {
            listDao.getList(targetCard.listId)
        } else {
            listDao.getList(targetListId)
        } ?: return null

        // 최하위 카드 조회
        val bottomCard = cardDao.getCardInListToBottom(targetList.id)

        return if (bottomCard == null) {
            // 보드에 리스트가 없는 경우

            CardMoveResult.ReorderedCardMove(
                listOf(
                    CardMoveResponseDto(
                        listId = targetListId,
                        cardId = targetCard.id,
                        myOrder = DEFAULT_TOP_ORDER
                    )
                )
            )
        } else if (bottomCard.myOrder >= Long.MAX_VALUE) {
            // 의도적으로 재시도 로직 실행
            val orderInfoList = generateUniqueOrderWithRetry(
                targetCard = targetCard,
                targetIndex = -1,
                targetList = targetList,
                baseOrder = -1000L,
                prevOrder = bottomCard.myOrder,
                nextOrder = null
            )
            CardMoveResult.ReorderedCardMove(orderInfoList)
        } else if (targetCard.myOrder == bottomCard.myOrder && targetList.id == targetCard.listId) {
            // 이미 최하단에 위치한 경우
            CardMoveResult.ReorderedCardMove(
                listOf(
                    CardMoveResponseDto(
                        listId = targetList.id,
                        cardId = targetCard.id,
                        myOrder = targetCard.myOrder
                    )
                )
            )
        } else {
            // 기준 순서 값 계산
            val baseOrder = min(
                bottomCard.myOrder + LARGE_INCREMENT,
                bottomCard.myOrder + ((Long.MAX_VALUE - bottomCard.myOrder) * MOVE_BOTTOM_ORDER_RATIO).roundToLong()
            )

            // 고유 순서 값 생성 및 재배치 체크
            val orderInfoList = generateUniqueOrderWithRetry(
                targetCard = targetCard,
                targetIndex = -1,
                targetList = targetList,
                baseOrder = baseOrder,
                prevOrder = bottomCard.myOrder,
                nextOrder = null
            )

            if (orderInfoList.size > 1) {
                CardMoveResult.ReorderedCardMove(orderInfoList)
            } else {
                // 단일 카드 이동
                val newOrder = orderInfoList.first().myOrder

                CardMoveResult.ReorderedCardMove(
                    listOf(
                        CardMoveResponseDto(
                            listId = targetListId,
                            cardId = targetCard.id,
                            myOrder = newOrder
                        )
                    )
                )
            }
        }
    }

    override suspend fun moveCardBetween(
        cardId: Long,
        previousCardId: Long,
        nextCardId: Long,
        isConnection: Boolean
    ): CardMoveResult? {

        var targetCard = cardDao.getCard(cardId) ?: return null

        // 동일한 ID가 있는지 확인하여, 동일한 경우 현재 카드의 순서 값으로 반환
        if (cardId == previousCardId || cardId == nextCardId || previousCardId == nextCardId) {
            return CardMoveResult.ReorderedCardMove(
                listOf(
                    CardMoveResponseDto(
                        listId = targetCard.listId,
                        cardId = targetCard.id,
                        myOrder = targetCard.myOrder
                    )
                )
            )
        }

        // 이전 카드와 다음 카드 조회
        val previousCard = cardDao.getCard(previousCardId) ?: return null
        val nextCard = cardDao.getCard(nextCardId) ?: return null

        // 두 카드가 동일한 리스트에 있는지 확인
        validateCardsInSameList(previousCard, nextCard)

        // 이전 이후의 리스트가 붙어있는지 확인
        if (previousCard.listId != nextCard.listId) {
            return CardMoveResult.ReorderedCardMove(
                listOf(
                    CardMoveResponseDto(
                        listId = targetCard.listId,
                        cardId = targetCard.id,
                        myOrder = targetCard.myOrder
                    )
                )
            )
        }

        // 이전 카드와 다음 카드의 순서값을 가져옴
        val prevOrder = previousCard.myOrder
        val nextOrder = nextCard.myOrder
        val gap = nextOrder - prevOrder

        // 간격이 클 경우 고정된 간격값을 적용하고, 작을 경우 중간값 사용
        val baseOrder = if (gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP) {
            prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
        } else {
            (prevOrder + nextOrder) / HALF_DIVIDER
        }

        // 전체 순서에서 이전 리스트와 다음 리스트가 인접해 있는지 확인
        val allCards = cardDao.getAllCardsInList(targetCard.listId)
        val previousIndex = allCards.indexOf(previousCard)
        val nextIndex = allCards.indexOf(nextCard)
        val targetIndex = previousIndex + 1

        if (previousIndex != -1 && nextIndex != -1 && nextIndex != previousIndex + 1) {
            return CardMoveResult.ReorderedCardMove(
                listOf(
                    CardMoveResponseDto(
                        listId = targetCard.listId,
                        cardId = targetCard.id,
                        myOrder = targetCard.myOrder
                    )
                )
            )
        }

        val targetList = listDao.getList(previousCard.listId) ?: return null

        // 고유한 순서값 생성 후 재배치 필요 여부 체크
        val orderInfoList = generateUniqueOrderWithRetry(
            targetCard = targetCard,
            targetIndex = targetIndex,
            targetList = targetList,
            baseOrder = baseOrder,
            prevOrder = prevOrder,
            nextOrder = nextOrder
        )

        return if (orderInfoList.size > 1) {
            // 재배치 필요 - 전체 리스트 반환
            CardMoveResult.ReorderedCardMove(orderInfoList)
        } else {
            // 단일 카드 이동
            val newOrder = orderInfoList.first().myOrder

            CardMoveResult.ReorderedCardMove(
                listOf(
                    CardMoveResponseDto(
                        listId = previousCard.listId,
                        cardId = targetCard.id,
                        myOrder = newOrder
                    )
                )
            )
        }
    }

    /**
     * 고유 순서 값을 생성합니다.
     *
     * @param baseOrder 기준 순서 값
     * @param maxOffset 최대 오프셋 값
     * @return 고유 순서 값
     */
    private fun generateUniqueOrder(baseOrder: Long, maxOffset: Long): Long {
        val offset = Random.nextLong(0, maxOffset)
        return baseOrder + offset
    }

    /**
     * 고유성 보장을 위해 임의 간격 조정 로직을 수행하고, 필요 시 전체 카드의 순서를 재배치합니다.
     *
     * @param targetCard 이동 대상 카드
     * @param targetIndex 목표 인덱스
     * @param targetList 목표 리스트
     * @param baseOrder 기준 순서 값
     * @param prevOrder 이전 카드의 순서 값 (없을 경우 null)
     * @param nextOrder 다음 카드의 순서 값 (없을 경우 null)
     * @return 고유 순서 값을 포함하는 CardMoveResponseDto 리스트
     */
    private suspend fun generateUniqueOrderWithRetry(
        targetCard: CardEntity,
        targetIndex: Int,
        targetList: ListEntity,
        baseOrder: Long,
        prevOrder: Long?,
        nextOrder: Long?
    ): List<CardMoveResponseDto> {
        val maxAttempts = 1
        var attempt = 0
        var maxOffset = 100L

        // 맨 위로 이동할 경우: offset이 기존 최상위 order보다 크지 않도록 제한
        if (prevOrder == null && nextOrder != null) {
            maxOffset = min(maxOffset, nextOrder - baseOrder - 1)
        }
        // 두 리스트 사이에 배치할 경우: offset이 두 리스트의 중간값을 넘지 않도록 제한
        else if (prevOrder != null && nextOrder != null) {
            maxOffset = min(maxOffset, (nextOrder - prevOrder) / 2)
        }
        if (maxOffset < 1) {
            maxOffset = 1
        }

        var newOrder = generateUniqueOrder(baseOrder, maxOffset)

        while (attempt < maxAttempts) {
            if (newOrder <= 0 || newOrder >= Long.MAX_VALUE) {
                return reorderAllCardOrders(targetList, targetCard, targetIndex)
            }

            // 중복 여부 확인
            val isConflict = isOrderConflict(targetList, newOrder)

            if (!isConflict) {
                // 충돌 없음 - 고유 순서 값 반환
                return listOf(
                    CardMoveResponseDto(
                        listId = targetList.id,
                        cardId = targetCard.id,
                        myOrder = newOrder
                    )
                )
            } else {
                // 충돌 발생 - 새로운 순서 값 생성 시도
                val randomOffset = Random.nextLong(50, 150)
                newOrder = baseOrder + (attempt + 1) * 100L + randomOffset
                attempt++
            }
        }

        // 최대 시도 횟수 초과 - 전체 카드 재배치
        return reorderAllCardOrders(targetList, targetCard, targetIndex)
    }

    /**
     * 특정 보드 내에서 순서 값이 중복되는지 확인합니다.
     *
     * @param list 목표 리스트
     * @param order 확인할 순서 값
     * @return 중복되면 true, 아니면 false
     */
    private fun isOrderConflict(list: ListEntity, order: Long): Boolean {
        val conflictExists = cardDao.checkCardInListExistMyOrder(list.id, order)
        return conflictExists
    }

    /**
     * 두 카드가 동일한 리스트에 있는지 확인합니다.
     *
     * @param previousCard 이전 카드
     * @param nextCard 다음 카드
     */
    private fun validateCardsInSameList(previousCard: CardEntity, nextCard: CardEntity) {
        if (previousCard.listId != nextCard.listId) {
            throw IllegalArgumentException("이전 카드와 다음 카드는 동일한 리스트에 있어야 합니다.")
        }
    }

    private suspend fun reorderAllCardOrders(
        list: ListEntity,
        prevCard: CardEntity,
        targetIndex: Int
    ): List<CardMoveResponseDto> {
        // 카드의 리스트 이동
        val targetCard = prevCard.copy(listId = list.id)

        // 해당 리스트의 모든 카드를 myOrder 기준으로 오름차순 정렬하여 조회
        val cards = cardDao.getAllCardsInList(list.id).toMutableList()

        // targetIndex가 -1일 경우 가장 아래로 삽입
        if (targetIndex == -1) {
            cards.remove(targetCard)
            cards.add(targetCard) // 가장 마지막에 삽입
        } else {
            cards.remove(targetCard)
            if (targetIndex in 0..cards.size) {
                cards.add(targetIndex, targetCard) // 특정 위치에 삽입
            } else {
                throw IndexOutOfBoundsException("targetIndex ${targetIndex}가 리스트 ${list.id}의 범위를 벗어났습니다.")
            }
        }

        // 초기 값 설정 및 결과 리스트 생성
        var newOrder = DEFAULT_TOP_ORDER
        val orderInfoList = mutableListOf<CardMoveResponseDto>()

        for (card in cards) {
            orderInfoList.add(
                CardMoveResponseDto(
                    cardId = card.id,
                    listId = list.id,
                    myOrder = newOrder
                )
            )
            newOrder += REORDER_GAP
        }

        return orderInfoList
    }
}