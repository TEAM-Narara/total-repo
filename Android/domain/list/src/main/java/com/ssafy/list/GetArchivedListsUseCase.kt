package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.model.list.ListResponseDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArchivedListsUseCase @Inject constructor(private val listRepository: ListRepository) {

    suspend operator fun invoke(boardId: Long): Flow<List<ListResponseDto>> {
        return listRepository.getArchivedLists(boardId)
    }

}
