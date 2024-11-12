package com.ssafy.network.source.card

import com.ssafy.model.attachment.AttachmentResponseDto
import com.ssafy.model.card.CardLabelUpdateDto
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.member.SimpleCardMemberDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardLabelDTO
import kotlinx.coroutines.flow.Flow

interface CardDataSource {

    suspend fun createCard(cardRequestDto: CardRequestDto): Flow<Unit>

    suspend fun deleteCard(cardId: Long): Flow<Unit>

    suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto
    ): Flow<Unit>

    suspend fun updateCardMember(
        boardId: Long,
        simpleCardMemberDto: SimpleCardMemberDto
    ): Flow<SimpleCardMemberDto>

    suspend fun setCardArchive(cardId: Long): Flow<Unit>

    suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>>

    suspend fun createCardLabel(cardLabel: CardLabelDTO): Flow<Unit>

    suspend fun deleteCardLabel(id: Long): Flow<Unit>

    suspend fun updateCardLabel(id: Long, cardLabelUpdateDto: CardLabelUpdateDto): Flow<Unit>

    suspend fun createAttachment(attachment: AttachmentDTO): Flow<AttachmentResponseDto>

    suspend fun deleteAttachment(attachmentId: Long): Flow<Unit>

    suspend fun updateAttachmentToCover(attachmentId: Long): Flow<Unit>

}
