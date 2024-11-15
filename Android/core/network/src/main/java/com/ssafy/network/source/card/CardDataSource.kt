package com.ssafy.network.source.card

import com.ssafy.model.attachment.AttachmentResponseDto
import com.ssafy.model.card.CardDetailDto
import com.ssafy.model.card.CardMoveUpdateListRequestDTO
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.label.CreateCardLabelRequestDto
import com.ssafy.model.label.UpdateCardLabelActivateRequestDto
import com.ssafy.model.member.SimpleCardMemberDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.nullable.UpdateCardWithNull
import kotlinx.coroutines.flow.Flow

interface CardDataSource {

    suspend fun createCard(cardRequestDto: CardRequestDto): Flow<CardDetailDto>

    suspend fun deleteCard(cardId: Long): Flow<Unit>

    suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto
    ): Flow<Unit>

    suspend fun moveCard(
        listId: Long,
        cardMoveUpdateListRequestDTO: CardMoveUpdateListRequestDTO
    ): Flow<Unit>

    suspend fun updateCard(
        cardId: Long,
        updateCardWithNull: UpdateCardWithNull
    ): Flow<Unit>

    suspend fun updateCardMember(
        boardId: Long,
        simpleCardMemberDto: SimpleCardMemberDto
    ): Flow<SimpleCardMemberDto>

    suspend fun setCardArchive(cardId: Long): Flow<Unit>

    suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>>

    suspend fun createCardLabel(createCardLabelRequestDto: CreateCardLabelRequestDto): Flow<CardLabelDTO>

    suspend fun deleteCardLabel(id: Long): Flow<Unit>

    suspend fun updateCardLabel(updateCardLabelActivateRequestDto: UpdateCardLabelActivateRequestDto): Flow<CardLabelDTO>

    suspend fun createAttachment(attachment: AttachmentDTO): Flow<AttachmentResponseDto>

    suspend fun deleteAttachment(attachmentId: Long): Flow<Unit>

    suspend fun updateAttachmentToCover(attachmentId: Long): Flow<Unit>

    suspend fun getAlertCard(cardId: Long, memberId: Long): Flow<Boolean>

    suspend fun setAlertCard(cardId: Long, memberId: Long): Flow<Boolean>

    suspend fun setCardPresenter(cardId: Long, memberId: Long): Flow<Boolean>
}
