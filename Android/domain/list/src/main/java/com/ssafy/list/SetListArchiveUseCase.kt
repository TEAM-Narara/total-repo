package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetListArchiveUseCase @Inject constructor(private val listRepository: ListRepository) {

    suspend operator fun invoke(
        listId: Long,
        isConnected: Boolean
    ): Flow<Unit> {
        return listRepository.setListArchive(listId, isConnected)
    }

}
