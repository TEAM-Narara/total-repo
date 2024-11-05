package com.ssafy.data.repository.comment

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.comment.UpdateCommentDto
import com.ssafy.network.source.comment.CommentDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val commentDataSource: CommentDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CommentRepository {

    override suspend fun createComment(
        commentRequestDto: CommentRequestDto,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                commentDataSource.createComment(commentRequestDto).toFlow()
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 생성하는 로직을 추가해주세요.")
            }
        }

    override suspend fun deleteComment(commentId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                commentDataSource.deleteComment(commentId).toFlow()
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 삭제하는 로직을 추가해주세요.")
            }
        }

    override suspend fun updateComment(
        commentId: Long,
        content: String,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            commentDataSource.updateComment(commentId, UpdateCommentDto(content)).toFlow()
        } else {
            TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
        }
    }

}
