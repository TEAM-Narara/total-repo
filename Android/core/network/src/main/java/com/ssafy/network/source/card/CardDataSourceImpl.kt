package com.ssafy.network.source.card

import com.ssafy.model.attachment.AttachmentResponseDto
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.label.CreateCardLabelRequestDto
import com.ssafy.model.label.UpdateCardLabelActivateRequestDto
import com.ssafy.model.member.SimpleCardMemberDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.network.api.CardAPI
import com.ssafy.network.api.CardLabelAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import com.ssafy.network.util.S3ImageUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CardDataSourceImpl @Inject constructor(
    private val cardAPI: CardAPI,
    private val cardLabelAPI: CardLabelAPI,
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

    override suspend fun createCardLabel(createCardLabelRequestDto: CreateCardLabelRequestDto): Flow<CardLabelDTO> =
        safeApiCall { cardLabelAPI.createCardLabel(createCardLabelRequestDto) }.toFlow()

    override suspend fun deleteCardLabel(id: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCardLabel(
        updateCardLabelActivateRequestDto: UpdateCardLabelActivateRequestDto
    ): Flow<CardLabelDTO> =
        safeApiCall { cardLabelAPI.updateLabelActivate(updateCardLabelActivateRequestDto) }.toFlow()

    override suspend fun createAttachment(attachment: AttachmentDTO): Flow<AttachmentResponseDto> {
        val key = "${attachment.id}/${attachment.url}"
        if (attachment.url.isNotBlank()) s3ImageUtil.uploadS3Image(attachment.url, key)
        return safeApiCall { cardAPI.createAttachment(attachment.cardId, key) }.toFlow()
    }

    override suspend fun deleteAttachment(attachmentId: Long): Flow<Unit> =
        safeApiCall { cardAPI.deleteAttachment(attachmentId) }.toFlow()


    override suspend fun updateAttachmentToCover(attachmentId: Long): Flow<Unit> =
        safeApiCall { cardAPI.updateAttachmentToCover(attachmentId) }.toFlow()


    override suspend fun getAlertCard(cardId: Long, memberId: Long): Flow<Boolean> =
        safeApiCall { cardAPI.getAlertCard(cardId, memberId) }.toFlow()

    override suspend fun setAlertCard(cardId: Long, memberId: Long): Flow<Boolean> =
        safeApiCall { cardAPI.setAlertCard(cardId, memberId) }.toFlow()

    override suspend fun setCardPresenter(cardId: Long, memberId: Long): Flow<Boolean> =
        safeApiCall {
            cardAPI.setRepresentativeCard(mapOf("cardId" to cardId, "memberId" to memberId))
        }.toFlow()
}
