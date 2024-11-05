package com.ssafy.network.source.comment

import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.comment.UpdateCommentDto
import com.ssafy.network.api.CommentAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class CommentDataSourceImpl @Inject constructor(
    private val commentAPI: CommentAPI
) : CommentDataSource {

    override suspend fun createComment(commentRequestDto: CommentRequestDto): Response<ApiResponse<Unit>> =
        commentAPI.createComment(commentRequestDto)

    override suspend fun deleteComment(commentId: Long): Response<ApiResponse<Unit>> =
        commentAPI.deleteComment(commentId)

    override suspend fun updateComment(
        commentId: Long,
        updateCommentDto: UpdateCommentDto
    ): Response<ApiResponse<Unit>> =
        commentAPI.updateComment(commentId, updateCommentDto)

}
