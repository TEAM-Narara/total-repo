package com.ssafy.list

import com.ssafy.data.repository.list.ListRepository
import com.ssafy.model.search.DueDate.DUE_IN_THE_NEXT_DAY
import com.ssafy.model.search.DueDate.DUE_IN_THE_NEXT_MONTH
import com.ssafy.model.search.DueDate.DUE_IN_THE_NEXT_WEEK
import com.ssafy.model.search.DueDate.NO_DUE_DATE
import com.ssafy.model.search.DueDate.OVERDUE
import com.ssafy.model.search.SearchParameters
import javax.inject.Inject

class GetLocalScreenListsInCardsFilterUseCase @Inject constructor(
    private val listRepository: ListRepository,
) {
    suspend operator fun invoke(boardId: Long, searchParameters: SearchParameters) {
        listRepository.getLocalScreenListsInCardsFilter(
            boardId = boardId,
            includeNoRepresentative = 0,// searchParameters.noMember.int,
            memberIdsEmpty = searchParameters.members.isEmpty().int,
            memberIds = searchParameters.members.toList(),
            noLimitDate = searchParameters.dueDates.contains(NO_DUE_DATE).int,
            expireDate = searchParameters.dueDates.contains(OVERDUE).int,
            deadlineDateType = when (searchParameters.dueDates.firstOrNull {
                it != NO_DUE_DATE && it != OVERDUE
            }) {
                null -> 0
                DUE_IN_THE_NEXT_DAY -> 1
                DUE_IN_THE_NEXT_WEEK -> 2
                DUE_IN_THE_NEXT_MONTH -> 3
                else -> 0
            },
            includeNoLabel = 0,//searchParameters.noLabel.int,
            labelIdsEmpty = searchParameters.labels.isEmpty().int,
            cardLabelIds = searchParameters.labels.toList(),
            keyword = searchParameters.searchText,
        )
    }
}

private val Boolean.int: Int
    get() = if (this) 1 else 0