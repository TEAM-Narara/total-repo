package com.ssafy.data.repository.comment

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toDto
import com.ssafy.database.dao.ReplyDao
import com.ssafy.database.dto.ReplyEntity
import com.ssafy.database.dto.piece.ReplyCount
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toDto
import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.comment.UpdateCommentDto
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ReplyDTO
import com.ssafy.model.with.ReplyWithMemberDTO
import com.ssafy.network.source.comment.CommentDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val commentDataSource: CommentDataSource,
    private val replyDao: ReplyDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CommentRepository {

    override suspend fun createComment(
        commentRequestDto: CommentRequestDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            commentDataSource.createComment(commentRequestDto).map { 5 }
        } else {
            flowOf(replyDao.insertReply(ReplyEntity(
                content = commentRequestDto.content,
                cardId = commentRequestDto.cardId,
                isStatus = DataStatus.CREATE,
                createAt = System.currentTimeMillis(),
                // TODO 내 아이디 가져오기
                memberId = 0L
            )))

        }
    }

    override suspend fun deleteComment(commentId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val reply = replyDao.getReply(commentId)

            if(reply != null) {
                if (isConnected) {
                    commentDataSource.deleteComment(commentId)
                } else {
                    val result = when(reply.isStatus) {
                        DataStatus.CREATE ->
                            replyDao.deleteReply(reply)
                        else ->
                            replyDao.updateReply(reply.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun updateComment(
        commentId: Long,
        content: String,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        val reply = replyDao.getReply(commentId)

        if(reply != null) {
            if (isConnected) {
                commentDataSource.updateComment(commentId, UpdateCommentDto(content))
            } else {
                val result = when(reply.isStatus) {
                    DataStatus.STAY ->
                        replyDao.updateReply(reply.copy(isStatus = DataStatus.UPDATE, content = content))
                    DataStatus.CREATE, DataStatus.UPDATE  ->
                        replyDao.updateReply(reply.copy(content = content))
                    DataStatus.DELETE -> { }
                }

                flowOf(result)
            }
        } else {
            flowOf(Unit)
        }
    }

    override suspend fun getLocalScreenCommentList(cardId: Long): Flow<List<ReplyWithMemberDTO>> =
        withContext(ioDispatcher) {
            replyDao.getAllReplies(cardId)
                .map { entities -> entities.map { it.toDto() } }
        }

    override suspend fun getLocalCreateReply(): List<ReplyDTO>  =
        withContext(ioDispatcher) {
            replyDao.getLocalCreateReplies()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationReply(): List<ReplyDTO> =
        withContext(ioDispatcher) {
            replyDao.getLocalOperationReplies()
                .map { it.toDTO() }
        }

    override suspend fun getReplyCounts(cardIds: List<Long>): Flow<List<ReplyCount>> =
        withContext(ioDispatcher) {
            replyDao.getReplyCounts(cardIds)
        }
}

