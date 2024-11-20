package com.ssafy.data.repository.sync

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.comment.CommentRepository
import com.ssafy.data.repository.list.ListRepository
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.background.CoverDto
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.BoardInListDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ReplyDTO
import com.ssafy.model.with.WorkspaceInBoardDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteManager @Inject constructor(
    private val workspaceRepository: WorkspaceRepository,
    private val boardRepository: BoardRepository,
    private val listRepository: ListRepository,
    private val cardRepository: CardRepository,
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
) {
    private val isConnected: Boolean = false

    suspend fun deleteMemberBackground(memberId: Long, list: List<CoverDto>) = list.forEach {
        memberRepository.deleteMemberBackground(memberId, it.id, isConnected)
    }

    suspend fun deleteFromWorkspaceList(list: List<WorkspaceInBoardDTO>) = list.forEach {
        workspaceRepository.deleteWorkspace(it.id, isConnected)
        deleteFromBoard(it.boards)
    }

    suspend fun deleteFromBoard(list: List<BoardInListDTO>) = list.forEach {
        boardRepository.deleteBoard(it.id, isConnected)
        deleteFromList(it.lists)
        deleteLabel(it.labels)
    }

    private suspend fun deleteLabel(list: List<LabelDTO>) = list.forEach {
        boardRepository.deleteLabel(it.labelId, isConnected)
    }

    suspend fun deleteFromList(list: List<ListInCardsDTO>) = list.forEach {
        listRepository.deleteList(it.id, isConnected)
        deleteFromCard(it.cards)
    }

    suspend fun deleteFromCard(list: List<CardAllInfoDTO>) = list.forEach {
        cardRepository.deleteCard(it.id, isConnected)
        deleteFromCardLabel(it.cardLabels)
        deleteFromCardAttachment(it.cardAttachment)
        deleteFromComment(it.cardReplies)
    }

    suspend fun deleteFromCardLabel(list: List<CardLabelDTO>) = list.forEach {
        cardRepository.deleteCardLabel(it.cardLabelId, isConnected)
    }

    suspend fun deleteFromCardAttachment(list: List<AttachmentDTO>) = list.forEach {
        cardRepository.deleteAttachment(it.id, isConnected)
    }

    suspend fun deleteFromComment(list: List<ReplyDTO>) = list.forEach {
        commentRepository.deleteComment(it.id, isConnected)
    }

}
