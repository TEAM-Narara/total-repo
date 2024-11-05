package com.ssafy.network.api

import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.comment.CommentResponseDto
import com.ssafy.model.comment.UpdateCommentDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentAPI {

    @POST("api/v1/reply")
    suspend fun createComment(@Body commentRequestDto: CommentRequestDto): Response<ApiResponse<CommentResponseDto>>

    @DELETE("api/v1/reply/{replyId}")
    suspend fun deleteComment(@Path("replyId") id: Long): Response<ApiResponse<Unit>>

    @PATCH("api/v1/reply/{replyId}")
    suspend fun updateComment(
        @Path("replyId") id: Long,
        @Body updateCommentDto: UpdateCommentDto
    ): Response<ApiResponse<CommentResponseDto>>

}
