package com.ssafy.data.socket.board.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.image.ImageStorage
import com.ssafy.data.socket.board.model.attachment.AddCardAttachmentRequestDto
import com.ssafy.data.socket.board.model.attachment.DeleteCardAttachmentRequestDto
import com.ssafy.data.socket.board.model.attachment.EditCardAttachmentCoverRequestDto
import com.ssafy.database.dao.AttachmentDao
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dto.AttachmentEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentService @Inject constructor(
    private val cardDao: CardDao,
    private val attachmentDao: AttachmentDao,
    private val imageStorage: ImageStorage,
    private val gson: Gson
) {
    suspend fun addCardAttachment(data: JsonObject) {
        val dto = gson.fromJson(data, AddCardAttachmentRequestDto::class.java)

        imageStorage.saveAll(key = dto.imgURL) { path ->
            attachmentDao.insertAttachment(
                AttachmentEntity(
                    id = dto.attachmentId,
                    cardId = dto.cardId,
                    url = path ?: "",
                    type = dto.type,
                    isCover = dto.isCover,
                )
            )
        }
    }

    suspend fun deleteCardAttachment(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteCardAttachmentRequestDto::class.java)
        val before =
            attachmentDao.getAttachment(dto.attachmentId) ?: throw Exception("존재하지 않는 첨부파일 입니다.")

        imageStorage.delete(before.url)

        attachmentDao.deleteAttachmentById(dto.attachmentId)
    }

    suspend fun editCardAttachmentCover(data: JsonObject) {
        val dto = gson.fromJson(data, EditCardAttachmentCoverRequestDto::class.java)

        val attachment = attachmentDao.getAttachment(dto.attachmentId) ?: throw Exception("존재하지 않는 첨부파일 입니다.")
        val beforeCard = cardDao.getCard(dto.cardId) ?: throw Exception("존재하지 않는 카드 입니다.")

        cardDao.updateCard(
            beforeCard.copy(
                coverType = dto.type,
                coverValue = attachment.url
            )
        )
    }
}