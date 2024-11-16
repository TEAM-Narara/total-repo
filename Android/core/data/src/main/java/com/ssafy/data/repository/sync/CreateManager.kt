package com.ssafy.data.repository.sync

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.comment.CommentRepository
import com.ssafy.data.repository.list.ListRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.background.Cover
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
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ReplyDTO
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val boardRepository: BoardRepository,
    private val listRepository: ListRepository,
    private val cardRepository: CardRepository,
    private val commentRepository: CommentRepository,
) {
    private val isConnected: Boolean = true

    suspend fun createBoard(workspaceId: Long, boardList: List<BoardInListDTO>) {
        if (workspaceId < 0) return

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
                visibility = Visibility.valueOf(board.visibility)
            )

            val boardID = boardRepository.createBoard(memberId, boardDTO, isConnected).first()
            val lists = board.lists
            val labels = board.labels
            val boardMembers = board.boardMembers.filter { it.memberId != memberId }
            val isBoardMyWatch = board.isBoardMyWatch

            createLabels(boardID, labels)
            createBoardMembers(boardID, boardMembers)
            if (isBoardMyWatch) boardRepository.toggleBoardWatch(memberId, boardID, isConnected)


            createList(boardID, lists)
        }
    }

    suspend fun createList(boardId: Long, lists: List<ListInCardsDTO>) {
        if (boardId < 0) return

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

    suspend fun createCards(listId: Long, cards: List<CardAllInfoDTO>) {
        if (listId < 0) return

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
//            createCardMembers(cardId, card.cardMembers)
            createCardLabels(cardId, card.cardLabels)
            if (card.cardMemberAlarm) cardRepository.setCardAlertStatus(cardId, memberId)

            createCardAttachments(cardId, card.cardAttachment)
            createComments(cardId, card.cardReplies)
        }
    }

    suspend fun createCardLabels(cardId: Long, labels: List<CardLabelDTO>) {
        if (cardId < 0) return

        labels.forEach { label: CardLabelDTO ->
            val dto = CreateCardLabelRequestDto(
                cardId = cardId,
                labelId = label.labelId,
            )
            cardRepository.createCardLabel(dto, isConnected)
        }
    }

    // 카드의 담당자를 할당하는 것입니다.
    suspend fun createCardMembers(cardId: Long, members: List<CardMemberDTO>) {
        if (cardId < 0) return

        members.forEach { member: CardMemberDTO ->
            cardRepository.setCardPresenter(cardId, member.memberId)
        }
    }

    suspend fun createCardAttachments(cardId: Long, attachments: List<AttachmentDTO>) {
        if (cardId < 0) return

        attachments.forEach { attachment: AttachmentDTO ->
            val dto = AttachmentDTO(
                cardId = cardId,
                url = attachment.url,
            )
            cardRepository.createAttachment(dto, isConnected)
        }
    }

    suspend fun createComments(cardId: Long, comments: List<ReplyDTO>) {
        if (cardId < 0) return

        val memberId = dataStoreRepository.getUser().memberId
        comments.forEach { comment: ReplyDTO ->
            val dto = CommentRequestDto(
                cardId = cardId,
                content = comment.content
            )

            commentRepository.createComment(memberId, dto, isConnected)
        }
    }

    private suspend fun createLabels(boardId: Long, labels: List<LabelDTO>) {
        if (boardId < 0) return

        labels.forEach { label: LabelDTO ->
            val createLabelRequestDto = CreateLabelRequestDto(
                name = label.labelName,
                color = label.labelColor,
            )
            boardRepository.createLabel(boardId, createLabelRequestDto, isConnected)
        }
    }

    private suspend fun createBoardMembers(boardId: Long, members: List<BoardMemberDTO>) {
        if (boardId < 0) return

        members.forEach { member: BoardMemberDTO ->
            boardRepository.createBoardMember(boardId, member.memberId, isConnected)
        }
    }

}