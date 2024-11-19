package com.ssafy.comment

import com.ssafy.data.repository.comment.CommentRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.comment.CommentRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val datastoreRepository: DataStoreRepository,
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(
        commentRequestDto: CommentRequestDto,
        isConnected: Boolean
    ): Flow<Long> {
        val memberId = datastoreRepository.getUser().memberId
        return commentRepository.createComment(memberId, commentRequestDto, isConnected)
    }

}
