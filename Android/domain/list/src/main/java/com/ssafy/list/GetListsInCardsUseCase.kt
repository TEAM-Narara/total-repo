package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.model.with.ListInCard
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListsInCardsUseCase @Inject constructor(private val listRepository: ListRepository) {

    suspend operator fun invoke(
        boardId: Long,
    ): Flow<List<ListInCard>> {
        return listRepository.getLocalScreenListsInCards(boardId)
    }

}
