package com.ssafy.comment

import com.ssafy.data.repository.comment.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(
        commentId: Long,
        content: String,
        isConnected: Boolean
    ): Flow<Unit> {
        return commentRepository.updateComment(commentId, content, isConnected)
    }

}
