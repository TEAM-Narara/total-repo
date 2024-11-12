package com.ssafy.data.repository.order

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.list.ListRepository
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
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToLong

@Singleton
class ListMyOrderRepositoryImpl @Inject constructor(
    private val listDao: ListDao,
    private val boardDao: BoardDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ListMyOrderRepository {
    // 맨 위로 옮김
    override suspend fun moveListToTop(listId: Long, isConnection: Boolean): ListMoveResult? {
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
            generateUniqueOrderWithRetry(targetList, 0, board, baseOrder)

        if (topList == null) {
            // TODO
            boardDao.updateBoard(board.copy(
                lastListOrder = orderInfoList.first().myOrder
            ))
        }

        return ListMoveResult.ReorderedListMove(orderInfoList)
    }

    override suspend fun moveListToBottom(listId: Long, isConnection: Boolean): ListMoveResult? {
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

        val baseOrder = bottomList?.let { list ->
            val minLimit = (list.myOrder * MOVE_BOTTOM_ORDER_RATIO).roundToLong()
            val calculatedOrder = max(list.myOrder - LARGE_INCREMENT, minLimit)
            calculatedOrder
        } ?: DEFAULT_TOP_ORDER

        val orderInfoList: List<ListMoveResponseDto> =
            generateUniqueOrderWithRetry(targetList, -1, board, baseOrder)

        // TODO
        boardDao.updateBoard(board.copy(
            lastListOrder = orderInfoList.first().myOrder
        ))

        return ListMoveResult.ReorderedListMove(orderInfoList)
    }

    override suspend fun moveListBetween(
        memberId: Long,
        listId: Long,
        previousListId: Long,
        nextListId: Long,
        isConnection: Boolean
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

        val prevOrder: Long = previousList.myOrder
        val nextOrder: Long = nextList.myOrder
        val gap = nextOrder - prevOrder

        val baseOrder: Long = if ((gap > MAX_INSERTION_DISTANCE_FOR_FIXED_GAP)
        ) prevOrder + MAX_INSERTION_DISTANCE_FOR_FIXED_GAP
        else (prevOrder + nextOrder) / HALF_DIVIDER

        val board = boardDao.getBoard(targetList.boardId) ?: return null
        val previousBoard = boardDao.getBoard(previousList.boardId) ?: return null

        val sortedLists = listDao.getAllListsInBoard(board.id)

        val targetIndex = sortedLists.indexOf(previousList) + 1

        val orderInfoList: List<ListMoveResponseDto> = generateUniqueOrderWithRetry(
            targetList,
            targetIndex,
            previousBoard,
            baseOrder
        )

        return ListMoveResult.ReorderedListMove(orderInfoList)
    }

    // 고유성 보장을 위해 임의 간격 조정 로직 추가
    private fun generateUniqueOrder(baseOrder: Long): Long {
        val gap: Long = LARGE_INCREMENT / 100 // LARGE_INCREMENT의 1%를 기본 간격으로 사용
        val offset = System.nanoTime() % gap
        val uniqueOrder = baseOrder + offset

        return uniqueOrder
    }

    private suspend fun generateUniqueOrderWithRetry(
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
                return reorderAllListOrders(board, targetList, targetIndex)
            }

            if (!isOrderConflict(board, newOrder)) {
                // 변경된 값만 반환,
                return listOf(
                    ListMoveResponseDto(
                        listId = targetList.id,
                        myOrder = newOrder
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
        return reorderAllListOrders(board, targetList, targetIndex)
    }

    // 리스트 순서 중복 확인 메서드
    private suspend fun isOrderConflict(board: BoardEntity, order: Long): Boolean {
        val conflictExists: Boolean = listDao.checkListInBoardExistMyOrder(board.id, order)

        return conflictExists
    }

    private suspend fun reorderAllListOrders(
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

        // TODO
        boardDao.updateBoard(board.copy(lastListOrder = newOrder - REORDER_GAP))

        return orderInfoList
    }
}