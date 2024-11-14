package com.ssafy.data.repository.order

import com.ssafy.data.repository.order.MoveConst.DEFAULT_TOP_ORDER
import com.ssafy.data.repository.order.MoveConst.HALF_DIVIDER
import com.ssafy.data.repository.order.MoveConst.LARGE_INCREMENT
import com.ssafy.data.repository.order.MoveConst.MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
import com.ssafy.data.repository.order.MoveConst.MOVE_BOTTOM_ORDER_RATIO
import com.ssafy.data.repository.order.MoveConst.MOVE_TOP_ORDER_RATIO
import com.ssafy.data.repository.order.MoveConst.REORDER_GAP
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.ListEntity
import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

@Singleton
class ListMyOrderRepositoryImpl @Inject constructor(
    private val listDao: ListDao,
    private val boardDao: BoardDao
): ListMyOrderRepository {

    // 맨 위로 옮김
    override suspend fun moveListToTop(listId: Long): ListMoveResult? {
        val targetList = listDao.getList(listId) ?: return null

        val board = boardDao.getBoard(targetList.boardId) ?: return null

        val topList = listDao.getListInBoardToTop(boardId = board.id)

        // 이미 최상단
        if (topList != null && targetList.myOrder == topList.myOrder) {
            return ListMoveResult.ReorderedListMove(
                listOf(
                    ListMoveResponseDto(
                        targetList.id,
                        targetList.myOrder
                    )
                )
            )
        }

        val baseOrder = topList?.let { list ->
            val maxLimit = (list.myOrder * MOVE_TOP_ORDER_RATIO).roundToLong()
            val calculatedOrder = max(list.myOrder - LARGE_INCREMENT, maxLimit)
            calculatedOrder
        } ?: DEFAULT_TOP_ORDER

        val orderInfoList: List<ListMoveResponseDto> =
            generateUniqueOrderWithRetry(targetList, 0, board, baseOrder, null, topList?.myOrder)

        return ListMoveResult.ReorderedListMove(orderInfoList)
    }

    override suspend fun moveListToBottom(listId: Long): ListMoveResult? {
        val targetList = listDao.getList(listId) ?: return null

        val board = boardDao.getBoard(targetList.boardId) ?: return null

        val bottomList = listDao.getListInBoardToBottom(boardId = board.id)

        // 이미 최하단
        if (bottomList != null && targetList.myOrder == bottomList.myOrder) {
            return ListMoveResult.ReorderedListMove(
                listOf(
                    ListMoveResponseDto(
                        targetList.id,
                        targetList.myOrder
                    )
                )
            )
        }

        val baseOrder = bottomList?.let {
            val minLimit = it.myOrder + ((Long.MAX_VALUE - it.myOrder) * MOVE_BOTTOM_ORDER_RATIO).toLong()
            min(it.myOrder + LARGE_INCREMENT, minLimit)
        } ?: DEFAULT_TOP_ORDER

        val orderInfoList = generateUniqueOrderWithRetry(
            targetList, -1, board, baseOrder, bottomList?.myOrder, null)

        return ListMoveResult.ReorderedListMove(orderInfoList)
    }

    override suspend fun moveListBetween(
        listId: Long,
        previousListId: Long,
        nextListId: Long
    ): ListMoveResult? {
        val targetList = listDao.getList(listId) ?: return null

        // 동일한 ID가 있는지 확인하여, 동일한 경우 현재 리스트의 순서 값으로 반환
        if (listId == previousListId || listId == nextListId || previousListId == nextListId) {
            return ListMoveResult.ReorderedListMove(
                listOf(
                    ListMoveResponseDto(
                        targetList.id,
                        targetList.myOrder
                    )
                )
            )
        }

        val previousList = listDao.getList(previousListId) ?: return null

        val nextList = listDao.getList(nextListId) ?: return null

        if (previousList.boardId != nextList.boardId) {
            return ListMoveResult.ReorderedListMove(
                listOf(
                    ListMoveResponseDto(targetList.id, targetList.myOrder)
                )
            )
        }

        val sortedLists = listDao.getAllListsInBoard(targetList.boardId)
        val previousIndex: Int = sortedLists.indexOf(previousList)
        val nextIndex: Int = sortedLists.indexOf(nextList)
        val targetIndex = previousIndex + 1

        if (previousIndex != -1 && nextIndex != -1 && nextIndex != previousIndex + 1) {
            return ListMoveResult.ReorderedListMove(
                listOf(
                    ListMoveResponseDto(targetList.id, targetList.myOrder)
                )
            )
        }

        val prevOrder: Long = previousList.myOrder
        val nextOrder: Long = nextList.myOrder
        val gap = nextOrder - prevOrder

        val baseOrder: Long = if ((gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
        ) prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
        else (prevOrder + nextOrder) / HALF_DIVIDER

        val previousBoard = boardDao.getBoard(previousList.boardId) ?: return null

        val orderInfoList: List<ListMoveResponseDto> = generateUniqueOrderWithRetry(
            targetList,
            targetIndex,
            previousBoard,
            baseOrder,
            prevOrder,
            nextOrder
        )

        return ListMoveResult.ReorderedListMove(orderInfoList)
    }

    // 고유성 보장을 위해 임의 간격 조정 로직 추가
    private fun generateUniqueOrder(baseOrder: Long, maxOffset: Long): Long {
        val offset = ThreadLocalRandom.current().nextLong(0, maxOffset)
        return baseOrder + offset
    }

    private fun generateUniqueOrderWithRetry(
        targetList: ListEntity,
        targetIndex: Int,
        board: BoardEntity,
        baseOrder: Long,
        prevOrder: Long?,
        nextOrder: Long?
    ): List<ListMoveResponseDto> {
        val maxAttempts = 1
        var attempt = 0
        var maxOffset: Long = 100

        // 맨 위로 이동할 경우: offset이 기존 최상위 order보다 크지 않도록 제한
        if (prevOrder == null && nextOrder != null) {
            maxOffset = min(maxOffset, nextOrder - baseOrder - 1)
        }
        // 두 리스트 사이에 배치할 경우: offset이 두 리스트의 중간값을 넘지 않도록 제한
        else if (prevOrder != null && nextOrder != null) {
            maxOffset = min(maxOffset, (nextOrder - prevOrder + 1) / 2)
        }
        if (maxOffset < 1) {
            maxOffset = 1
        }

        var newOrder = generateUniqueOrder(baseOrder, maxOffset)

        while (attempt < maxAttempts) {

            if (newOrder <= 0 || newOrder >= Long.MAX_VALUE) {
                return reorderAllListOrders(board, targetList, targetIndex)
            }

            if (!isOrderConflict(board, newOrder)) {
                return listOf(
                    ListMoveResponseDto(
                        listId = targetList.id,
                        myOrder = newOrder
                    )
                )
            } else {
                val randomOffset = ThreadLocalRandom.current().nextLong(0, maxOffset)
                newOrder = baseOrder + randomOffset

                attempt++
            }
        }

        return reorderAllListOrders(board, targetList, targetIndex)
    }

    // 리스트 순서 중복 확인 메서드
    private fun isOrderConflict(board: BoardEntity, order: Long): Boolean {
        val conflictExists: Boolean = listDao.checkListInBoardExistMyOrder(board.id, order)

        return conflictExists
    }

    private fun reorderAllListOrders(
        board: BoardEntity,
        targetList: ListEntity,
        targetIndex: Int
    ): List<ListMoveResponseDto> {
        val lists: MutableList<ListEntity> = listDao.getAllListsInBoard(board.id).toMutableList()

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
        val orderInfoList: MutableList<ListMoveResponseDto> = ArrayList()

        for (list in lists) {
            orderInfoList.add(ListMoveResponseDto(list.id, newOrder))
            newOrder += REORDER_GAP
        }

        return orderInfoList
    }
}