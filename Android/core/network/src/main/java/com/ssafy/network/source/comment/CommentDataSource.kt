package com.ssafy.network.source.comment

import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.comment.CommentResponseDto
import com.ssafy.model.comment.UpdateCommentDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response

interface CommentDataSource {

    suspend fun createComment(commentRequestDto: CommentRequestDto): Response<ApiResponse<CommentResponseDto>>

    suspend fun deleteComment(commentId: Long): Response<ApiResponse<Unit>>

    suspend fun updateComment(
        commentId: Long,
        updateCommentDto: UpdateCommentDto
    ): Response<ApiResponse<CommentResponseDto>>

}
