package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.model.list.UpdateListRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateListUseCase @Inject constructor(private val listRepository: ListRepository) {

    suspend operator fun invoke(
        updateListRequestDto: UpdateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> {
        return listRepository.updateList(updateListRequestDto, isConnected)
    }

}
