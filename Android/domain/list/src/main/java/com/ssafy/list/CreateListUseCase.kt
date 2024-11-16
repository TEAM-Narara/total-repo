package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.data.repository.order.ListMyOrderRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.list.CreateListRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateListUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val listRepository: ListRepository,
    private val listMyOrderRepository: ListMyOrderRepository,
) {

    suspend operator fun invoke(
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Long> {
        val memberId = dataStoreRepository.getUser().memberId
        return listRepository.createList(memberId, createListRequestDto, isConnected).also {
            if (!isConnected) {
                val id = it.first()
                val listMoveResult = listMyOrderRepository.moveListToBottom(id)

                if (listMoveResult != null) {
                    listRepository.moveList(
                        boardId = createListRequestDto.boardId,
                        listMoveUpdateRequestDTO = listMoveResult.toListMoveUpdateRequestDto(),
                        isConnected = isConnected,
                    )
                }
            }
        }
    }

}
