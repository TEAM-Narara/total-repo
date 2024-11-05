package com.ssafy.comment

import com.ssafy.data.repository.comment.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(commentId: Long, isConnected: Boolean): Flow<Unit> {
        return commentRepository.deleteComment(commentId, isConnected)
    }

}
