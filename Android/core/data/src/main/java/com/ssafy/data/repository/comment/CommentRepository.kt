package com.ssafy.data.repository.comment

import com.ssafy.model.comment.CommentRequestDto
import kotlinx.coroutines.flow.Flow

interface CommentRepository {

    suspend fun createComment(
        commentRequestDto: CommentRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun deleteComment(commentId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateComment(
        commentId: Long,
        content: String,
        isConnected: Boolean
    ): Flow<Unit>

}
