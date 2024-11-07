package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.model.list.CreateListRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateListUseCase @Inject constructor(private val listRepository: ListRepository) {

    suspend operator fun invoke(
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Long> {
        return listRepository.createList(createListRequestDto, isConnected)
    }

}
