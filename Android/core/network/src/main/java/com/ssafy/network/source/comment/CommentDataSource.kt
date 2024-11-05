package com.ssafy.network.source.comment

import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.comment.UpdateCommentDto
import kotlinx.coroutines.flow.Flow

interface CommentDataSource {

    suspend fun createComment(commentRequestDto: CommentRequestDto): Flow<Unit>

    suspend fun deleteComment(commentId: Long): Flow<Unit>

    suspend fun updateComment(
        commentId: Long,
        updateCommentDto: UpdateCommentDto
    ): Flow<Unit>

}
