package com.ssafy.data.repository

import com.ssafy.data.repository.MoveConst.DEFAULT_TOP_ORDER
import com.ssafy.data.repository.MoveConst.HALF_DIVIDER
import com.ssafy.data.repository.MoveConst.LARGE_INCREMENT
import com.ssafy.data.repository.MoveConst.MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
import com.ssafy.data.repository.MoveConst.MOVE_BOTTOM_ORDER_RATIO
import com.ssafy.data.repository.MoveConst.MOVE_TOP_ORDER_RATIO
import com.ssafy.data.repository.MoveConst.REORDER_GAP
import com.ssafy.data.repository.list.ListRepository
import com.ssafy.data.repository.list.ListRepositoryImpl
import com.ssafy.data.socket.board.service.ListService
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.ListEntity
import java.lang.reflect.Member
import java.util.Optional
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.math.roundToLong

data class ListMoveResponseDto(val listId: Long, val myOrder: Long)

sealed class ListMoveResult {
    data class SingleListMove(val response: ListMoveResponseDto) : ListMoveResult()
    data class ReorderedListMove(val responses: List<ListMoveResponseDto>) : ListMoveResult()
}

class ListMoveServiceImpl(
    private val listDao: ListDao,
    private val boardDao: BoardDao,
    private val listRepository: ListRepository
) {
    // 맨 위로 옮김
    suspend fun moveListToTop(member: Member, listId: Long): ListMoveResult? {
        val targetList = listDao.getList(listId) ?: return null

        val board = boardDao.getBoard(targetList.boardId) ?: return null

        val topList = listDao.getListInBoardToTop(boardId = board.id)

        // 이미 최상단
        if (topList != null && targetList.myOrder == topList.myOrder) {
            return ListMoveResult.SingleListMove(
                ListMoveResponseDto(
                    targetList.id,
                    targetList.myOrder
                )
            )
        }

        val baseOrder = topList?.let { list ->
            val maxLimit = (list.myOrder * MOVE_TOP_ORDER_RATIO).roundToLong()
            val calculatedOrder = max(list.myOrder - LARGE_INCREMENT, maxLimit)
            calculatedOrder
        } ?: DEFAULT_TOP_ORDER

        val orderInfoList: List<ListMoveResponseDto> =
            generateUniqueOrderWithRetry(targetList, 0, board, baseOrder)

        if (orderInfoList.size > 1) {
            return ListMoveResult.ReorderedListMove(orderInfoList)
        }

        listDao.updateList(targetList.copy(
            myOrder = orderInfoList.first().myOrder
        ))

        if (topList == null) {
            board.setLastListOrder(orderInfoList.first().myOrder)
        }

        return ListMoveResult.SingleListMove(orderInfoList.first())
    }


    suspend fun moveListToBottom(member: Member, listId: Long): ListMoveResult? {
        val targetList = listDao.getList(listId) ?: return null

        val board = boardDao.getBoard(targetList.boardId) ?: return null

        val bottomList = listDao.getListInBoardToBottom(boardId = board.id)

        // 이미 최하단
        if (bottomList != null && targetList.myOrder == bottomList.myOrder) {
            return ListMoveResult.SingleListMove(
                ListMoveResponseDto(
                    targetList.id,
                    targetList.myOrder
                )
            )
        }

        val baseOrder = bottomList?.let { list ->
            val minLimit = (list.myOrder * MOVE_BOTTOM_ORDER_RATIO).roundToLong()
            val calculatedOrder = max(list.myOrder - LARGE_INCREMENT, minLimit)
            calculatedOrder
        } ?: DEFAULT_TOP_ORDER

        val orderInfoList: List<ListMoveResponseDto> =
            generateUniqueOrderWithRetry(targetList, -1, board, baseOrder)

        if (orderInfoList.size > 1) {
            return ListMoveResult.ReorderedListMove(orderInfoList)
        }

        listDao.updateList(targetList.copy(
            myOrder = orderInfoList.first().myOrder
        ))

        board.setLastListOrder(orderInfoList.first().myOrder)

        return ListMoveResult.SingleListMove(orderInfoList.first())
    }

    @Transactional
    fun moveListBetween(
        member: Member,
        listId: Long,
        previousListId: Long,
        nextListId: Long
    ): ListMoveResult {
        log.info(
            "moveListBetween 메서드 시작 - listId: {}, previousListId: {}, nextListId: {}",
            listId,
            previousListId,
            nextListId
        )

        // 동일한 ID가 있는지 확인하여, 동일한 경우 현재 리스트의 순서 값으로 반환
        if (listId == previousListId || listId == nextListId || previousListId == nextListId) {
            val targetList: List<*> = listRepository.findById(listId)
                .orElseThrow { NotFoundEntityException(listId, "리스트") }
            log.info(
                "같은 ID 감지 - listId: {}, previousListId: {}, nextListId: {}",
                listId,
                previousListId,
                nextListId
            )
            return SingleListMove(ListMoveResponseDto(targetList.getId(), targetList.getMyOrder()))
        }

        val targetList: List<*> = listRepository.findById(listId)
            .orElseThrow { NotFoundEntityException(listId, "리스트") }
        log.info(
            "타겟 리스트 조회 완료 - targetListId: {}, currentOrder: {}",
            targetList.getId(),
            targetList.getMyOrder()
        )

        listService.checkBoardMember(targetList, member, MOVE_LIST)
        log.info(
            "보드 접근 권한 확인 완료 - memberId: {}, boardId: {}",
            member.getId(),
            targetList.getBoard().getId()
        )

        val previousList: List<*> = listRepository.findById(previousListId)
            .orElseThrow { NotFoundEntityException(previousListId, "이전 리스트") }
        val nextList: List<*> = listRepository.findById(nextListId)
            .orElseThrow { NotFoundEntityException(nextListId, "다음 리스트") }

        val prevOrder: Long = previousList.getMyOrder()
        val nextOrder: Long = nextList.getMyOrder()
        val gap = nextOrder - prevOrder

        log.info("중간 위치 계산 - prevOrder: {}, nextOrder: {}, gap: {}", prevOrder, nextOrder, gap)

        val baseOrder: Long = if ((gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
        ) prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
        else (prevOrder + nextOrder) / HALF_DIVIDER
        log.info("기준 순서 값 설정 - baseOrder: {}", baseOrder)

        val targetIndex: Int = listRepository.findAllByBoardOrderByMyOrderAsc(targetList.getBoard())
            .indexOf(previousList) + 1

        val orderInfoList: List<ListMoveResponseDto> = generateUniqueOrderWithRetry(
            targetList,
            targetIndex,
            previousList.getBoard(),
            baseOrder
        )
        log.info("고유 순서 값 생성 및 재배치 체크 완료 - orderInfoList size: {}", orderInfoList.size)

        if (orderInfoList.size > 1) {
            log.info("재배치 필요 - 전체 리스트 반환")
            return ReorderedListMove(orderInfoList)
        }

        targetList.setMyOrder(orderInfoList.getFirst().myOrder())
        listRepository.save(targetList)
        log.info("리스트 중간에 성공적으로 배치 - newOrder: {}", orderInfoList.getFirst().myOrder())

        return SingleListMove(orderInfoList.getFirst())
    }


    // 고유성 보장을 위해 임의 간격 조정 로직 추가
    private fun generateUniqueOrder(baseOrder: Long): Long {
        val gap: Long = LARGE_INCREMENT / 100 // LARGE_INCREMENT의 1%를 기본 간격으로 사용
        val offset = System.nanoTime() % gap
        val uniqueOrder = baseOrder + offset
        log.info(
            "generateUniqueOrder - baseOrder: {}, gap: {}, offset: {}, uniqueOrder: {}",
            baseOrder,
            gap,
            offset,
            uniqueOrder
        )
        return uniqueOrder
    }


    private fun generateUniqueOrderWithRetry(
        targetList: ListEntity,
        targetIndex: Int,
        board: BoardEntity,
        baseOrder: Long
    ): List<ListMoveResponseDto> {
        val maxAttempts = 2
        var attempt = 0
        var newOrder = generateUniqueOrder(baseOrder)

        while (attempt < maxAttempts) {
            if (newOrder <= 0 || newOrder >= Long.MAX_VALUE) {
                return listReorderService.reorderAllListOrders(board, targetList, targetIndex)
            }

            if (!isOrderConflict(board, newOrder)) {
                // 변경된 값만 반환,
                return java.util.List.of<ListMoveResponseDto>(
                    ListMoveResponseDto(
                        targetList.getId(),
                        newOrder
                    )
                )
            } else {
                // 랜덤 오프셋을 통해 순서 값 충돌을 방지하고, 여러 번의 시도를 통해 고유한 순서 값을 생성
                // 1. 50과 150 사이의 난수를 생성하여 `randomOffset`에 할당
                //    - 이 값은 `newOrder`에 더해져 기존 순서 값과의 충돌을 방지하는 역할을 합니다.
                //    - ThreadLocalRandom.current().nextLong(50, 150)은 50 이상 150 미만의 임의의 값을 생성합니다.
                // 2. 시도 횟수(`attempt`)에 따라 고유 순서 값을 다르게 적용
                //    - 시도가 진행될 때마다 `(attempt + 1) * 100L`를 계산하여 `baseOrder`에 추가
                //    - `attempt + 1`은 시도 횟수에 따라 증가하므로 매번 고유한 값을 보장할 수 있습니다.
                // 3. 최종적으로 `newOrder`는 `baseOrder + (attempt + 1) * 100L + randomOffset` 형태로 계산
                //    - 충돌이 발생해도 시도 횟수에 따라 순서 값이 바뀌면서 고유한 순서를 찾을 가능성이 높아집니다.

                val randomOffset = ThreadLocalRandom.current().nextLong(50, 150)
                newOrder = baseOrder + (attempt + 1) * 100L + randomOffset
                attempt++
            }
        }
        return listReorderService.reorderAllListOrders(board, targetList, targetIndex)
    }

    // 리스트 순서 중복 확인 메서드
    private fun isOrderConflict(board: Board, order: Long): Boolean {
        val conflictExists: Boolean = listRepository.existsByBoardAndMyOrder(board, order)
        log.info(
            "isOrderConflict - boardId: {}, order: {}, conflictExists: {}",
            board.getId(),
            order,
            conflictExists
        )
        return conflictExists
    }
}

class ListReorderServiceImpl(
    private val listDao: ListDao,
    private val listRepository: ListRepository
) {
    fun reorderAllListOrders(
        board: BoardEntity,
        targetList: ListEntity,
        targetIndex: Int
    ): List<ListMoveResponseDto> {
        val lists: MutableList<ListEntity> = listRepository.findAllByBoardOrderByMyOrderAsc(board)

        // targetIndex -1일 경우 가장 아래로 삽입
        if (targetIndex == -1) {
            lists.remove(targetList)
            lists.add(targetList) // 가장 마지막에 삽입
        } else {
            lists.remove(targetList)
            lists.add(targetIndex, targetList) // 특정 위치에 삽입
        }

        // 고정된 간격으로 리스트 순서 재설정
        var newOrder = DEFAULT_TOP_ORDER
        val orderInfoList: MutableList<ListMoveResponseDto> = ArrayList<ListMoveResponseDto>()
        for (list in lists) {
            list.setMyOrder(newOrder)
            orderInfoList.add(ListMoveResponseDto(list.getId(), newOrder))
            newOrder += REORDER_GAP
        }

        listRepository.saveAll(lists)
        board.setLastListOrder(newOrder)

        return orderInfoList
    }
}

object MoveConst {
    const val DEFAULT_TOP_ORDER: Long = 4000000000000000000L // 초기 순서 값
    const val MOVE_TOP_ORDER_RATIO: Double = 2.0 / 3.0
    const val MOVE_BOTTOM_ORDER_RATIO: Double = 1.0 / 3.0
    const val LARGE_INCREMENT: Long = 50000000000000000L

    const val MAX_INSERTION_DISTANCE_FOR_FIXED_GAP: Long = 10000000000000000L
    const val HALF_DIVIDER: Long = 2

    const val REORDER_GAP: Long = 10000000000000000L // 각 카드 사이 간
}
