package com.ssafy.network.source.card

import com.ssafy.model.attachment.AttachmentResponseDto
import com.ssafy.model.card.CardLabelUpdateDto
import com.ssafy.model.card.CardMoveUpdateRequestDTO
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.member.SimpleCardMemberDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.network.api.CardAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import com.ssafy.network.util.S3ImageUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CardDataSourceImpl @Inject constructor(
    private val cardAPI: CardAPI,
    private val s3ImageUtil: S3ImageUtil
) : CardDataSource {

    override suspend fun createCard(cardRequestDto: CardRequestDto): Flow<Unit> =
        safeApiCall { cardAPI.createCard(cardRequestDto) }.toFlow()

    override suspend fun deleteCard(cardId: Long): Flow<Unit> =
        safeApiCall { cardAPI.deleteCard(cardId) }.toFlow()

    override suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto
    ): Flow<Unit> = safeApiCall { cardAPI.updateCard(cardId, cardUpdateRequestDto) }.toFlow()

    override suspend fun moveCard(
        listId: Long,
        cardMoveUpdateRequestDTO: List<CardMoveUpdateRequestDTO>
    ): Flow<Unit> =
        safeApiCall { cardAPI.moveCard(listId, cardMoveUpdateRequestDTO) }.toFlow()

    override suspend fun updateCardMember(
        boardId: Long,
        simpleCardMemberDto: SimpleCardMemberDto
    ): Flow<SimpleCardMemberDto> {
        TODO("Not yet implemented")
    }

    override suspend fun setCardArchive(cardId: Long): Flow<Unit> =
        safeApiCall { cardAPI.setCardArchive(cardId) }.toFlow()

    override suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>> =
        safeApiCall { cardAPI.getArchivedCards(boardId) }.toFlow()

    override suspend fun createCardLabel(cardLabel: CardLabelDTO): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCardLabel(id: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCardLabel(
        id: Long,
        cardLabelUpdateDto: CardLabelUpdateDto
    ): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createAttachment(attachment: AttachmentDTO): Flow<AttachmentResponseDto> {
        val key = "${attachment.id}/${attachment.url}"
        if (attachment.url.isNotBlank()) s3ImageUtil.uploadS3Image(attachment.url, key)
        return safeApiCall { cardAPI.createAttachment(attachment.cardId, key) }.toFlow()
    }

    override suspend fun deleteAttachment(attachmentId: Long): Flow<Unit> =
        safeApiCall { cardAPI.deleteAttachment(attachmentId) }.toFlow()


    override suspend fun updateAttachmentToCover(attachmentId: Long): Flow<Unit> =
        safeApiCall { cardAPI.updateAttachmentToCover(attachmentId) }.toFlow()

}
