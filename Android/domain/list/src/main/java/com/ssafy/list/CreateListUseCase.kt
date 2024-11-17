package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.list.CreateListRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateListUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val listRepository: ListRepository,
) {

    suspend operator fun invoke(
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Long> {
        val memberId = dataStoreRepository.getUser().memberId
        return listRepository.createList(memberId, createListRequestDto, isConnected)
    }
}
