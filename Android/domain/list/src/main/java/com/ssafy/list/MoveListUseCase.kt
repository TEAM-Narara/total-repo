package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.data.repository.order.ListMoveResult
import com.ssafy.data.repository.order.ListMyOrderRepository
import com.ssafy.model.list.ListMoveUpdateRequestDTO
import javax.inject.Inject

class MoveListUseCase @Inject constructor(
    private val orderRepository: ListMyOrderRepository,
    private val listRepository: ListRepository
) {
    suspend operator fun invoke(
        boardId: Long,
        listId: Long,
        prevListId: Long?,
        nextListId: Long?,
        isConnected: Boolean,
    ) {
        val listMoveResult = if (prevListId == null) {
            orderRepository.moveListToTop(listId)
        } else if (nextListId == null) {
            orderRepository.moveListToBottom(listId)
        } else {
            orderRepository.moveListBetween(listId, prevListId, nextListId)
        }

        if (listMoveResult == null) return

        listRepository.moveList(
            boardId = boardId,
            listMoveUpdateRequestDTO = listMoveResult.toListMoveUpdateRequestDto(),
            isConnected = isConnected,
        )
    }
}

fun ListMoveResult.toListMoveUpdateRequestDto() = when (this) {
    is ListMoveResult.ReorderedListMove -> responses.map {
        ListMoveUpdateRequestDTO(
            it.listId,
            it.myOrder
        )
    }

    is ListMoveResult.SingleListMove -> listOf(
        ListMoveUpdateRequestDTO(
            response.listId,
            response.myOrder
        )
    )
}
