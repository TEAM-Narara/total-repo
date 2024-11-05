package com.ssafy.comment

import com.ssafy.data.repository.comment.CommentRepository
import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.comment.CommentResponseDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(
        commentRequestDto: CommentRequestDto,
        isConnected: Boolean
    ): Flow<Unit> {
        return commentRepository.createComment(commentRequestDto, isConnected)
    }

}
