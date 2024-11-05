package com.ssafy.network.source.comment

import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.comment.UpdateCommentDto
import com.ssafy.network.api.CommentAPI
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentDataSourceImpl @Inject constructor(
    private val commentAPI: CommentAPI
) : CommentDataSource {

    override suspend fun createComment(commentRequestDto: CommentRequestDto): Flow<Unit> =
        commentAPI.createComment(commentRequestDto).toFlow()

    override suspend fun deleteComment(commentId: Long): Flow<Unit> =
        commentAPI.deleteComment(commentId).toFlow()

    override suspend fun updateComment(
        commentId: Long,
        updateCommentDto: UpdateCommentDto
    ): Flow<Unit> =
        commentAPI.updateComment(commentId, updateCommentDto).toFlow()

}
