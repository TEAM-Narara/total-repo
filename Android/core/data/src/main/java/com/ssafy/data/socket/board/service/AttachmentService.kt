package com.ssafy.data.socket.board.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.socket.board.model.attachment.AddCardAttachmentRequestDto
import com.ssafy.data.socket.board.model.attachment.DeleteCardAttachmentRequestDto
import com.ssafy.data.socket.board.model.attachment.EditCardAttachmentCoverRequestDto
import com.ssafy.database.dao.AttachmentDao
import com.ssafy.database.dto.AttachmentEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentService @Inject constructor(
    private val attachmentDao: AttachmentDao,
    private val gson: Gson
) {
    suspend fun addCardAttachment(data: JsonObject) {
        val dto = gson.fromJson(data, AddCardAttachmentRequestDto::class.java)
        // TODO : 이미지 저장 로직 구현
        attachmentDao.insertAttachment(
            AttachmentEntity(
                id = dto.attachmentId,
                cardId = dto.cardId,
                url = dto.imgURL,
                type = dto.type,
                isCover = dto.isCover,
            )
        )
    }

    suspend fun deleteCardAttachment(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteCardAttachmentRequestDto::class.java)
        // TODO : 이미지 삭제 로직 구현
        attachmentDao.deleteAttachmentById(dto.attachmentId)
    }

    suspend fun editCardAttachmentCover(data: JsonObject) {
        val dto = gson.fromJson(data, EditCardAttachmentCoverRequestDto::class.java)
        val before = attachmentDao.getAttachment(dto.attachmentId) ?: throw Exception("존재하지 않는 첨부파일 입니다.")
        attachmentDao.updateAttachment(
            before.copy(
                isCover = dto.isCover
            )
        )
    }
}