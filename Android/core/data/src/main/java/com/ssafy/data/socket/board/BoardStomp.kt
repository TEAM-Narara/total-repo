package com.ssafy.data.socket.board

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.socket.BaseStompManager
import com.ssafy.data.socket.board.service.AttachmentService
import com.ssafy.data.socket.board.service.BoardService
import com.ssafy.data.socket.board.service.CardService
import com.ssafy.data.socket.board.service.ListService
import com.ssafy.data.socket.board.service.ReplyService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardStomp @Inject constructor(
    private val stomp: BaseStompManager,
    private val boardService: BoardService,
    private val listService: ListService,
    private val cardService: CardService,
    private val replyService: ReplyService,
    private val attachmentService: AttachmentService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private var _job: Job? = null

    fun connect(boardId: Long) {
        disconnect()
        _job = CoroutineScope(ioDispatcher).launch {
            stomp.subscribe("board/$boardId").stateIn(this).collect {
                when (it.action) {
                    "ADD_BOARD_MEMBER" -> boardService.addBoardMember(it.data)
                    "EDIT_BOARD_MEMBER" -> boardService.editBoardMember(it.data)
                    "DELETE_BOARD_MEMBER" -> boardService.deleteBoardMember(it.data)
                    "EDIT_BOARD_WATCH" -> boardService.editBoardWatch(it.data)
                    "ADD_BOARD_LABEL" -> boardService.addBoardLabel(it.data)
                    "EDIT_BOARD_LABEL" -> boardService.editBoardLabel(it.data)
                    "DELETE_BOARD_LABEL" -> boardService.deleteBoardLabel(it.data)
                    "ADD_LIST" -> listService.addList(it.data)
                    "EDIT_LIST" -> listService.editList(it.data)
                    "EDIT_LIST_ARCHIVE" -> listService.editListArchive(it.data)
                    "ADD_CARD" -> cardService.addCard(it.data)
                    "EDIT_CARD" -> cardService.editCard(it.data)
                    "ARCHIVE_CARD" -> cardService.archiveCard(it.data)
                    "DELETE_CARD" -> cardService.deleteCard(it.data)
                    "ADD_CARD_MEMBER" -> cardService.addCardMember(it.data)
                    "DELETE_CARD_MEMBER" -> cardService.deleteCardMember(it.data)
                    "ADD_CARD_LABEL" -> cardService.addCardLabel(it.data)
                    "DELETE_CARD_LABEL" -> cardService.deleteCardLabel(it.data)
                    "ADD_REPLY" -> replyService.addReply(it.data)
                    "EDIT_REPLY" -> replyService.editReply(it.data)
                    "DELETE_REPLY" -> replyService.deleteReply(it.data)
                    "ADD_CARD_ATTACHMENT" -> attachmentService.addCardAttachment(it.data)
                    "DELETE_CARD_ATTACHMENT" -> attachmentService.deleteCardAttachment(it.data)
                    "EDIT_CARD_ATTACHMENT_COVER" -> attachmentService.editCardAttachmentCover(it.data)
                    else -> {}
                }
            }
        }
    }

    fun disconnect() {
        _job?.cancel()
        _job = null
    }
}