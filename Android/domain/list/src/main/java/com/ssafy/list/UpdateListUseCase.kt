package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.model.list.ListRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateListUseCase @Inject constructor(private val listRepository: ListRepository) {

    suspend operator fun invoke(
        listId: Long,
        listRequestDto: ListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> {
        return listRepository.updateList(listId, listRequestDto, isConnected)
    }

}
