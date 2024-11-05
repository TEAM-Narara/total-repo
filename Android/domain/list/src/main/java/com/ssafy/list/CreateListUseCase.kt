package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.model.list.ListRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateListUseCase @Inject constructor(private val listRepository: ListRepository) {

    suspend operator fun invoke(
        listRequestDto: ListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> {
        return listRepository.createList(listRequestDto, isConnected)
    }

}
