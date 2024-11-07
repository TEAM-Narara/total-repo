package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.model.with.ListInCardsDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListsUseCase @Inject constructor(private val listRepository: ListRepository) {
    suspend operator fun invoke(boardId: Long): Flow<List<ListInCardsDTO>> {
        TODO("listRepository에서 card 데이터까지 포함된 list 만들어지면 받아오기")
    }
}
