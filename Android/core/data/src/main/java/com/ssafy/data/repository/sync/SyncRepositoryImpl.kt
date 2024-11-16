package com.ssafy.data.repository.sync

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.comment.CommentRepository
import com.ssafy.data.repository.list.ListRepository
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.ReplyEntity
import com.ssafy.database.dto.bitmask.UpdateCardBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateListBitmaskDTO
import com.ssafy.database.dto.piece.getNullColumnBoard
import com.ssafy.database.dto.piece.getNullColumnCard
import com.ssafy.database.dto.piece.getNullColumnList
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.background.CoverDto
import com.ssafy.model.label.UpdateCardLabelActivateRequestDto
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val createManager: CreateManager,
    private val deleteManager: DeleteManager,
    private val dataStoreRepository: DataStoreRepository,
    private val memberRepository: MemberRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val boardRepository: BoardRepository,
    private val listRepository: ListRepository,
    private val cardRepository: CardRepository,
    private val commentRepository: CommentRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SyncRepository {

    private val isConnected: Boolean = true

    private suspend fun syncMemberBackgroundList() {
        val memberId = dataStoreRepository.getUser().memberId
        val create: List<CoverDto> = memberRepository.getLocalCreateMemberBackgrounds()
//        val change: List<CoverDto> = memberRepository.getLocalOperationMemberBackgrounds()
        // TODO 멤버의 배경은 수정, 삭제될 수 없습니다.
        runCatching {
            create.forEach {
                memberRepository.createMemberBackground(memberId, it, isConnected)
            }

//            change.forEach {
//                if (it.isStatus == DataStatus.CREATE)
//                    memberRepository.createMemberBackground(memberId, it, isConnected)
//                else if (it.isStatus == DataStatus.DELETE)
//                    memberRepository.deleteMemberBackground(memberId, it.id, isConnected)
//            }

        }.also { deleteManager.deleteMemberBackground(memberId, create) }
    }

    private suspend fun syncWorkspaceList() {
        val create: List<WorkspaceInBoardDTO> = workspaceRepository.getLocalCreateWorkspaceList()
        val change: List<WorkSpaceDTO> = workspaceRepository.getLocalOperationWorkspaceList()

        runCatching {
            create.forEach { workspaceInBoard: WorkspaceInBoardDTO ->
                val workspaceId = workspaceInBoard.id
                val workspaceName = workspaceInBoard.name
                val boardList = workspaceInBoard.boards

                val newWorkspaceId = workspaceRepository
                    .createWorkspace(workspaceId, workspaceName, isConnected).first()

                createManager.createBoard(newWorkspaceId, boardList)
            }

            change.forEach { workSpaceDTO: WorkSpaceDTO ->
                val workspaceId = workSpaceDTO.workspaceId
                val workspaceName = workSpaceDTO.name
                workspaceRepository.updateWorkspace(workspaceId, workspaceName, isConnected)
            }
        }.also { deleteManager.deleteFromWorkspaceList(create) }
    }

    private suspend fun syncBoardList() {
        val create: List<BoardInListDTO> = boardRepository.getLocalCreateBoardList()
        val change: List<BoardEntity> = boardRepository.getLocalOperationBoardList()

        runCatching {
            create.groupBy { it.workspaceId }
                .forEach { (workspaceId, boardList) ->
                    createManager.createBoard(workspaceId, boardList)
                }

            change.forEach { board: BoardEntity ->
                val boardBitmaskDTO = getNullColumnBoard(board.columnUpdate, board)
                boardRepository.updateBoard(board.id, boardBitmaskDTO)
            }
        }.also { deleteManager.deleteFromBoard(create) }
    }

    private suspend fun syncListList() {
        val create: List<ListInCardsDTO> = listRepository.getLocalCreateList()
        val change: List<ListEntity> = listRepository.getLocalOperationList()

        runCatching {
            create.groupBy { it.boardId }
                .forEach { (boardId, listList) -> createManager.createList(boardId, listList) }

            change.forEach { listEntity: ListEntity ->
                val listBitmaskDto: UpdateListBitmaskDTO =
                    getNullColumnList(listEntity.columnUpdate, listEntity)
                listRepository.updateList(listEntity.id, listBitmaskDto)
            }
        }.also { deleteManager.deleteFromList(create) }
    }

    private suspend fun syncCardList() {
        val create: List<CardAllInfoDTO> = cardRepository.getLocalCreateCard()
        val change: List<CardEntity> = cardRepository.getLocalOperationCard()

        runCatching {
            create.groupBy { it.listId }
                .forEach { (listId, cardList) -> createManager.createCards(listId, cardList) }

            change.forEach { cardEntity ->
                val cardBitmaskDTO: UpdateCardBitmaskDTO =
                    getNullColumnCard(cardEntity.columnUpdate, cardEntity)
                cardRepository.updateCard(cardEntity.id, cardBitmaskDTO)
            }
        }.also { deleteManager.deleteFromCard(create) }
    }

    private suspend fun syncCardMemberList() {
        val create: List<CardMemberDTO> = cardRepository.getLocalOperationCardMember()

        runCatching {
            create.groupBy { it.memberId }
                .forEach { (cardId, cardMemberList) ->
//                    createManager.createCardMembers(cardId, cardMemberList)
                }

        }
    }

    private suspend fun syncCardLabelList() {
        val create: List<CardLabelDTO> = cardRepository.getLocalCreateCardLabels()
        val change: List<CardLabelEntity> = cardRepository.getLocalOperationCardLabels()

        runCatching {
            create.groupBy { it.cardId }
                .forEach { (cardId, cardLabelList) ->
                    createManager.createCardLabels(
                        cardId,
                        cardLabelList
                    )
                }

            change.forEach { cardLabelEntity: CardLabelEntity ->
                val updateCardLabelDTO = UpdateCardLabelActivateRequestDto(
                    cardId = cardLabelEntity.id,
                    labelId = cardLabelEntity.labelId,
                    isActivated = cardLabelEntity.isActivated
                )
                cardRepository.updateCardLabel(updateCardLabelDTO, isConnected)
            }
        }.also { deleteManager.deleteFromCardLabel(create) }
    }

    private suspend fun syncCardAttachmentList() {
        val create: List<AttachmentDTO> = cardRepository.getLocalCreateAttachments()
        // TODO Attachment는 수정할 수 없습니다.

        runCatching {
            create.groupBy { it.cardId }
                .forEach { (cardId, attachmentList) ->
                    createManager.createCardAttachments(cardId, attachmentList)
                }
        }.also { deleteManager.deleteFromCardAttachment(create) }
    }

    private suspend fun syncCommentList() {
        val create: List<ReplyDTO> = commentRepository.getLocalCreateReply()
        val change: List<ReplyEntity> = commentRepository.getLocalOperationReply()

        runCatching {
            create.groupBy { it.cardId }
                .forEach { (cardId, commentList) ->
                    createManager.createComments(cardId, commentList)
                }

            change.forEach { commentRepository.updateComment(it.id, it.content, isConnected) }
        }.also { deleteManager.deleteFromComment(create) }
    }

    override suspend fun syncAll() = withContext(ioDispatcher) {
        memberRepository.addMember(dataStoreRepository.getUser())
        syncMemberBackgroundList()
        syncWorkspaceList()
        syncBoardList()
        syncListList()
        syncCardList()
        syncCardMemberList()
        syncCardLabelList()
        syncCardAttachmentList()
        syncCommentList()
    }

}
