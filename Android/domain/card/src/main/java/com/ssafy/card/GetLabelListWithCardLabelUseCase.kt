package com.ssafy.card

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.with.LabelWithCardLabelDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetLabelListWithCardLabelUseCase @Inject constructor(
    private val boardRepository: BoardRepository,
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(boardId: Long, cardId: Long): Flow<List<LabelWithCardLabelDTO>?> =
        combine(
            boardRepository.getLabels(boardId),
            cardRepository.getAllCardLabelsInCard(cardId)
        ) { labels, cardLabels ->
            labels.map { label ->
                LabelWithCardLabelDTO(
                    labelId = label.labelId,
                    boardId = label.boardId,
                    labelName = label.labelName,
                    labelColor = label.labelColor,
                    isActivated = cardLabels.firstOrNull { it.labelId == label.labelId }?.isActivated ?: false
                )
            }
        }
}