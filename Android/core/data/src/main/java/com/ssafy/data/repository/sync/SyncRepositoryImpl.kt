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
import com.ssafy.model.background.Cover
import com.ssafy.model.background.CoverDto
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.comment.CommentRequestDto
import com.ssafy.model.label.CreateCardLabelRequestDto
import com.ssafy.model.label.CreateLabelRequestDto
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.BoardInListDTO
import com.ssafy.model.with.BoardMemberDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.CoverType
import com.ssafy.model.with.DataStatus
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
        val change: List<CoverDto> = memberRepository.getLocalOperationMemberBackgrounds()

        runCatching {
            create.forEach {
                memberRepository.createMemberBackground(memberId, it, isConnected)
            }

            // 이게 change 가 있나?
            change.forEach {
                if (it.isStatus == DataStatus.CREATE)
                    memberRepository.createMemberBackground(memberId, it, isConnected)
                else if (it.isStatus == DataStatus.DELETE)
                    memberRepository.deleteMemberBackground(memberId, it.id, isConnected)
            }

        }.onSuccess {
            create.forEach { background ->
                memberRepository.deleteMemberBackground(memberId, background.id, isConnected)
            }
        }
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

                createBoard(newWorkspaceId, boardList)
            }

            change.forEach { workSpaceDTO: WorkSpaceDTO ->
                val workspaceId = workSpaceDTO.workspaceId
                val workspaceName = workSpaceDTO.name
                workspaceRepository.createWorkspace(workspaceId, workspaceName, isConnected)
            }
        }.onSuccess {
            create.forEach { workspace ->
                workspaceRepository.deleteWorkspace(workspace.id, false)
            }
        }
    }

    private suspend fun syncBoardList() {
        val create: List<BoardInListDTO> = boardRepository.getLocalCreateBoardList()
        val change: List<BoardEntity> = boardRepository.getLocalOperationBoardList()

        runCatching {
            create.groupBy { it.workspaceId }
                .forEach { (workspaceId, boardList) -> createBoard(workspaceId, boardList) }

            change.forEach { board: BoardEntity ->
                val boardBitmaskDTO = getNullColumnBoard(board.columnUpdate, board)
                boardRepository.updateBoard(board.id, boardBitmaskDTO)
            }
        }.onSuccess {
            create.forEach { board ->
                boardRepository.deleteBoard(board.id, false)
            }
        }
    }

    private suspend fun syncListList() {
        val create: List<ListInCardsDTO> = listRepository.getLocalCreateList()
        val change: List<ListEntity> = listRepository.getLocalOperationList()

        runCatching {
            create.groupBy { it.boardId }
                .forEach { (boardId, listList) -> createList(boardId, listList) }

            change.forEach { listEntity: ListEntity ->
                val listBitmaskDto: UpdateListBitmaskDTO =
                    getNullColumnList(listEntity.columnUpdate, listEntity)
                listRepository.updateList(listEntity.id, listBitmaskDto)
            }
        }.onSuccess {
            create.forEach { list ->
                listRepository.deleteList(list.id, false)
            }
        }
    }

    private suspend fun syncCardList() {
        val create: List<CardAllInfoDTO> = cardRepository.getLocalCreateCard()
        val change: List<CardEntity> = cardRepository.getLocalOperationCard()

        runCatching {
            create.groupBy { it.listId }
                .forEach { (listId, cardList) -> createCards(listId, cardList) }

            change.forEach { cardEntity ->
                val cardBitmaskDTO: UpdateCardBitmaskDTO =
                    getNullColumnCard(cardEntity.columnUpdate, cardEntity)
                cardRepository.updateCard(cardEntity.id, cardBitmaskDTO)
            }
        }.onSuccess {
            create.forEach { card ->
                cardRepository.deleteCard(card.id, false)
            }
        }
    }

    private suspend fun syncCardMemberList() {
        val create: List<CardMemberDTO> = cardRepository.getLocalOperationCardMember()

        runCatching {
            create.groupBy { it.memberId }
                .forEach { (cardId, cardMemberList) -> createCardMembers(cardId, cardMemberList) }

        }.onSuccess {
            create.forEach { cardMember ->
                cardRepository.deleteLocalOperationCardMember(cardMember.id)
            }
        }
    }

    private suspend fun syncCardLabelList() {
        val create: List<CardLabelDTO> = cardRepository.getLocalCreateCardLabels()
        val change: List<CardLabelEntity> = cardRepository.getLocalOperationCardLabels()

        runCatching {
            create.groupBy { it.cardId }
                .forEach { (cardId, cardLabelList) -> createCardLabels(cardId, cardLabelList) }

            change.forEach { cardLabelEntity: CardLabelEntity ->
                // TODO 이거 없음??
            }
        }.onSuccess {
            create.forEach { cardLabel ->
                cardRepository.deleteCardLabel(cardLabel.labelId, false)
            }
        }
    }

    private suspend fun syncCardAttachmentList() {
        val create: List<AttachmentDTO> = cardRepository.getLocalCreateAttachments()
        // TODO Attachment는 수정할 수 없습니다.

        runCatching {
            create.groupBy { it.cardId }
                .forEach { (cardId, attachmentList) ->
                    createCardAttachments(
                        cardId,
                        attachmentList
                    )
                }
        }.onSuccess {
            create.forEach { attachment ->
                cardRepository.deleteAttachment(attachment.id, false)
            }
        }
    }

    private suspend fun syncCommentList() {
        val create: List<ReplyDTO> = commentRepository.getLocalCreateReply()
        val change: List<ReplyEntity> = commentRepository.getLocalOperationReply()

        runCatching {
            create.groupBy { it.cardId }
                .forEach { (cardId, commentList) -> createComments(cardId, commentList) }

            change.forEach { commentRepository.updateComment(it.id, it.content, isConnected) }
        }.onSuccess {
            create.forEach { comment ->
                commentRepository.deleteComment(comment.id, false)
            }
        }
    }


    private suspend fun createBoard(workspaceId: Long, boardList: List<BoardInListDTO>) {
        val memberId = dataStoreRepository.getUser().memberId

        boardList.forEach { board: BoardInListDTO ->
            val coverType = board.coverType?.let { type -> CoverType.valueOf(type) }
                ?: CoverType.NONE
            val coverValue = board.coverValue ?: ""
            val cover = Cover(
                type = coverType,
                value = coverValue,
            )
            val boardDTO = BoardDTO(
                workspaceId = workspaceId,
                id = board.id,
                name = board.name,
                cover = cover,
                isClosed = board.isClosed,
                visibility = Visibility.valueOf(board.visibility),
            )

            val boardID = boardRepository.createBoard(memberId, boardDTO, isConnected).first()
            val lists = board.lists
            val labels = board.labels
            val boardMembers = board.boardMembers
            val isBoardMyWatch = board.isBoardMyWatch
            createList(boardID, lists)
            createLabels(boardID, labels)
            createBoardMembers(boardID, boardMembers)
            if (isBoardMyWatch) boardRepository.toggleBoardWatch(boardID, isConnected)
        }
    }

    private suspend fun createList(boardId: Long, lists: List<ListInCardsDTO>) {
        val memberId = dataStoreRepository.getUser().memberId

        lists.forEach { list: ListInCardsDTO ->
            val listDTO = CreateListRequestDto(
                boardId = boardId,
                listName = list.name,
            )

            val listId = listRepository.createList(memberId, listDTO, isConnected).first()
            val cards: List<CardAllInfoDTO> = list.cards
            createCards(listId, cards)
        }
    }

    private suspend fun createCards(listId: Long, cards: List<CardAllInfoDTO>) {
        val memberId = dataStoreRepository.getUser().memberId

        cards.forEach { card: CardAllInfoDTO ->
            val cardDTO = CardRequestDto(
                listId = listId,
                cardName = card.name,
            )

            val cardId = cardRepository.createCard(memberId, cardDTO, isConnected).first()
            val dto = CardUpdateRequestDto(
                name = card.name,
                description = card.description,
                startAt = card.startAt,
                endAt = card.endAt,
                cover = card.cover ?: Cover(),
            )

            cardRepository.updateCard(cardId, dto, isConnected)
            if (card.cardMemberAlarm) cardRepository.setCardAlertStatus(cardId, memberId)
            createCardLabels(cardId, card.cardLabels)
            createCardMembers(cardId, card.cardMembers)
            createCardAttachments(cardId, card.cardAttachment)
            createComments(cardId, card.cardReplies)
        }
    }

    private suspend fun createCardLabels(cardId: Long, labels: List<CardLabelDTO>) {
        labels.forEach { label: CardLabelDTO ->
            val dto = CreateCardLabelRequestDto(
                cardId = cardId,
                labelId = label.labelId,
            )
            cardRepository.createCardLabel(dto, isConnected)
        }
    }

    // 카드의 담당자를 할당하는 것입니다.
    private suspend fun createCardMembers(cardId: Long, members: List<CardMemberDTO>) {
        members.forEach { member: CardMemberDTO ->
            cardRepository.setCardPresenter(cardId, member.memberId)
        }
    }

    private suspend fun createCardAttachments(cardId: Long, attachments: List<AttachmentDTO>) {
        attachments.forEach { attachment: AttachmentDTO ->
            val dto = AttachmentDTO(
                cardId = cardId,
                url = attachment.url,
            )
            cardRepository.createAttachment(dto, isConnected)
        }
    }

    private suspend fun createComments(cardId: Long, comments: List<ReplyDTO>) {
        comments.forEach { comment: ReplyDTO ->
            val dto = CommentRequestDto(
                cardId = cardId,
                content = comment.content
            )
            commentRepository.createComment(dto, isConnected)
        }
    }

    private suspend fun createLabels(boardId: Long, labels: List<LabelDTO>) {
        labels.forEach { label: LabelDTO ->
            val createLabelRequestDto = CreateLabelRequestDto(
                name = label.labelName,
                color = label.labelColor,
            )
            boardRepository.createLabel(boardId, createLabelRequestDto, isConnected)
        }
    }

    private suspend fun createBoardMembers(boardId: Long, members: List<BoardMemberDTO>) {
        members.forEach { member: BoardMemberDTO ->
            boardRepository.createBoardMember(boardId, member.memberId, isConnected)
        }
    }

    override suspend fun syncAll() = withContext(ioDispatcher) {
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
