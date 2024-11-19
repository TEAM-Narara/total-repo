package com.ssafy.data.socket.board.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.socket.board.model.reply.AddReplyRequestDto
import com.ssafy.data.socket.board.model.reply.DeleteReplyRequestDto
import com.ssafy.data.socket.board.model.reply.EditReplyRequestDto
import com.ssafy.database.dao.ReplyDao
import com.ssafy.database.dto.ReplyEntity
import com.ssafy.model.with.DataStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReplyService @Inject constructor(
    private val replyDao: ReplyDao,
    private val gson: Gson
) {
    suspend fun addReply(data: JsonObject) {
        val dto = gson.fromJson(data, AddReplyRequestDto::class.java)
        replyDao.insertReply(
            ReplyEntity(
                id = dto.replyId,
                cardId = dto.cardId,
                memberId = dto.memberId,
                content = dto.content,
                createAt = dto.createdAt,
                updateAt = dto.updatedAt,
            )
        )
    }

    suspend fun editReply(data: JsonObject) {
        val dto = gson.fromJson(data, EditReplyRequestDto::class.java)
        val before = replyDao.getReply(dto.replyId) ?: throw Exception("존재하지 않는 댓글 입니다.")
        replyDao.updateReply(
            before.copy(
                content = dto.content,
                updateAt = dto.updatedAt,
                isStatus = DataStatus.STAY,
            )
        )
    }

    suspend fun deleteReply(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteReplyRequestDto::class.java)
        replyDao.deleteReply(dto.replyId)
    }
}