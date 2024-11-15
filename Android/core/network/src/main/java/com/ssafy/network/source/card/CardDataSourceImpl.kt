package com.ssafy.network.source.card

import com.ssafy.model.attachment.AttachmentResponseDto
import com.ssafy.model.card.CardDetailDto
import com.ssafy.model.card.CardMoveUpdateRequestDTO
import com.ssafy.model.card.CardMoveUpdateListRequestDTO
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.label.CreateCardLabelRequestDto
import com.ssafy.model.label.UpdateCardLabelActivateRequestDto
import com.ssafy.model.member.SimpleCardMemberDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CoverType
import com.ssafy.network.api.CardAPI
import com.ssafy.network.api.CardLabelAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import com.ssafy.network.util.S3ImageUtil
import com.ssafy.nullable.UpdateCardWithNull
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CardDataSourceImpl @Inject constructor(
    private val cardAPI: CardAPI,
    private val cardLabelAPI: CardLabelAPI,
    private val s3ImageUtil: S3ImageUtil
) : CardDataSource {

    override suspend fun createCard(cardRequestDto: CardRequestDto): Flow<CardDetailDto> =
        safeApiCall { cardAPI.createCard(cardRequestDto) }.toFlow()

    override suspend fun deleteCard(cardId: Long): Flow<Unit> =
        safeApiCall { cardAPI.deleteCard(cardId) }.toFlow()

    override suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto
    ): Flow<Unit> {
        val coverValue = cardUpdateRequestDto.cover.value
        val key = getAttachmentKey(cardId, coverValue)
        if (coverValue.isNotBlank()) {
            s3ImageUtil.uploadS3Image(coverValue, key)
            val newDto =
                cardUpdateRequestDto.copy(cover = cardUpdateRequestDto.cover.copy(value = key))
            return safeApiCall { cardAPI.updateCard(cardId, newDto) }.toFlow()
        } else {
            return safeApiCall { cardAPI.updateCard(cardId, cardUpdateRequestDto) }.toFlow()
        }
    }

    override suspend fun updateCard(
        cardId: Long,
        updateCardWithNull: UpdateCardWithNull
    ): Flow<Unit> {
        updateCardWithNull.cover?.let {
            if (it.type == CoverType.IMAGE) {
                val coverValue = it.value ?: return@let
                val key = getAttachmentKey(cardId, coverValue)
                s3ImageUtil.uploadS3Image(coverValue, key)
                val newDto = updateCardWithNull.copy(cover = it.copy(value = key))
                return safeApiCall { cardAPI.updateCard(cardId, newDto) }.toFlow()
            }
        }
        return safeApiCall { cardAPI.updateCard(cardId, updateCardWithNull) }.toFlow()
    }

    override suspend fun moveCard(
        listId: Long,
        cardMoveUpdateListRequestDTO: CardMoveUpdateListRequestDTO
    ): Flow<Unit> =
        safeApiCall { cardAPI.moveCard(listId, cardMoveUpdateListRequestDTO) }.toFlow()

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
        val key = getAttachmentKey(attachment.cardId, attachment.url)
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


    private fun getAttachmentKey(cardId: Long, path: String): String {
        val lastPath = path.substringAfterLast("/").substringBeforeLast(".jpg")
        return "$cardId/$lastPath"
    }

}
