package com.ssafy.data.repository.comment

import com.ssafy.database.dto.ReplyEntity
import com.ssafy.database.dto.piece.ReplyCount
import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.with.ReplyDTO
import com.ssafy.model.with.ReplyWithMemberDTO
import kotlinx.coroutines.flow.Flow

interface CommentRepository {

    suspend fun createComment(
        memberId: Long,
        commentRequestDto: CommentRequestDto,
        isConnected: Boolean
    ): Flow<Long>

    suspend fun deleteComment(commentId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateComment(
        commentId: Long,
        content: String,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun getLocalScreenCommentList(cardId: Long): Flow<List<ReplyWithMemberDTO>>

    suspend fun getLocalCreateReply(): List<ReplyDTO>

    suspend fun getLocalOperationReply(): List<ReplyEntity>

    suspend fun getReplyCounts(cardIds: List<Long>): Flow<List<ReplyCount>>
}
