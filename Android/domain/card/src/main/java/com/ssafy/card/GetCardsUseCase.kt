package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.comment.CommentRepository
import com.ssafy.model.background.Cover
import com.ssafy.model.card.CardDTO
import com.ssafy.model.card.CommentDTO
import com.ssafy.model.search.Label
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardsUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val commentRepository: CommentRepository,
) {

    suspend operator fun invoke(cardId: Long): Flow<CardDTO?> = combine(
        cardRepository.getCard(cardId),
        cardRepository.getCardRepresentativesInCard(cardId),
        cardRepository.getAllCardLabelsInCard(cardId),
        cardRepository.getCardWithListAndBoardName(cardId),
        cardRepository.getAllAttachments(cardId),
        cardRepository.getCardAlertStatus(cardId),
        commentRepository.getLocalScreenCommentList(cardId),
    ) { card, members, labels, cardWithListAndBoardName, attachments, isAlert, replies ->
        if (card == null || cardWithListAndBoardName == null) null
        else CardDTO(
            cardId = card.id,
            isWatching = isAlert,
            content = card.description,
            title = card.name,
            boardTitle = cardWithListAndBoardName.boardName,
            listTitle = cardWithListAndBoardName.listName,
            startDate = card.startAt,
            endDate = card.endAt,
            labels = labels.map { Label(it.labelName, it.labelColor) },
            members = members,
            cover = card.cover ?: Cover(),
            attachments = attachments,
            comments = replies.map {
                CommentDTO(
                    commentId = it.id,
                    userId = it.memberId,
                    nickname = it.memberNickname,
                    profileImageUrl = it.memberProfileImgUrl,
                    content = it.content,
                    createDate = it.createAt,
                    updateDate = it.updateAt
                )
            },
        )
    }
}

private inline fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> {
    return kotlinx.coroutines.flow.combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
        )
    }
}