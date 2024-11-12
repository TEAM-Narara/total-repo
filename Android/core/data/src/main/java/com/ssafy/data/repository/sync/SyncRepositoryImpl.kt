package com.ssafy.data.repository.sync

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.comment.CommentRepository
import com.ssafy.data.repository.list.ListRepository
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.background.CoverDto
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.BoardInListDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ReplyDTO
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val memberRepository: MemberRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val boardRepository: BoardRepository,
    private val listRepository: ListRepository,
    private val cardRepository: CardRepository,
    private val commentRepository: CommentRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SyncRepository {

    override suspend fun syncMemberBackgroundList() = withContext(ioDispatcher) {
        val createList: List<CoverDto> = memberRepository.getLocalCreateMemberBackgrounds()
        val changeList: List<CoverDto> = memberRepository.getLocalOperationMemberBackgrounds()

        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

    override suspend fun syncWorkspaceList() = withContext(ioDispatcher) {
        val create: List<WorkspaceInBoardDTO> = workspaceRepository.getLocalCreateWorkspaceList()
        val change: List<WorkSpaceDTO> = workspaceRepository.getLocalOperationWorkspaceList()

        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

    override suspend fun syncBoardList() = withContext(ioDispatcher) {
        val create: List<BoardInListDTO> = boardRepository.getLocalCreateBoardList()
        val change: List<BoardDTO> = boardRepository.getLocalOperationBoardList()

        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

    override suspend fun syncListList() {
        val create: List<ListInCardsDTO> = listRepository.getLocalCreateList()
        val change: List<ListResponseDto> = listRepository.getLocalOperationList()

        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

    override suspend fun syncCardList() = withContext(ioDispatcher) {
        val create: List<CardAllInfoDTO> = cardRepository.getLocalCreateCard()
        val change: List<CardResponseDto> = cardRepository.getLocalOperationCard()

        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

    override suspend fun syncCardMemberList() {
        val change: List<CardMemberDTO> = cardRepository.getLocalOperationCardMember()

        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

    override suspend fun syncCardLabelList() {
        val create: List<CardLabelDTO> = cardRepository.getLocalCreateCardLabels()
        val change: List<CardLabelDTO> = cardRepository.getLocalOperationCardLabels()

        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

    override suspend fun syncCardAttachmentList() {
        val create: List<AttachmentDTO> = cardRepository.getLocalCreateAttachments()
        val change: List<AttachmentDTO> = cardRepository.getLocalOperationAttachment()


        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

    override suspend fun syncCommentList() = withContext(ioDispatcher) {
        val create: List<ReplyDTO> = commentRepository.getLocalCreateReply()
        val change: List<ReplyDTO> = commentRepository.getLocalOperationReply()

        // TODO : 서버와 동기화 이후 변경 사항들 전부 삭제
    }

}
