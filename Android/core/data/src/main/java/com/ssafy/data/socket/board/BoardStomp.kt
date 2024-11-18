package com.ssafy.data.socket.board

import android.util.Log
import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.socket.BaseStompManager
import com.ssafy.data.socket.board.service.AttachmentService
import com.ssafy.data.socket.board.service.BoardService
import com.ssafy.data.socket.board.service.CardService
import com.ssafy.data.socket.board.service.ListService
import com.ssafy.data.socket.board.service.ReplyService
import com.ssafy.network.socket.StompMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.produceIn
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
    private var _boardID: Long? = null

    fun connect(boardId: Long) {
        if (_boardID == boardId) return
        _boardID = boardId

        disconnect()

        _job = CoroutineScope(ioDispatcher).launch {
            runCatching {
                stomp.subscribe("board/$boardId").buffer(Channel.BUFFERED)
                    .produceIn(this)
                    .consumeEach { message ->
                        Log.d("TAG", "consumeEach: $message")
                        runCatching {
                            handleMessage(message)
                        }.onFailure { e ->
                            e.printStackTrace()
                        }
                    }
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    private suspend fun handleMessage(message: StompMessage) = when (message.action) {
        "ADD_BOARD_MEMBER" -> boardService.addBoardMember(message.data)
        "EDIT_BOARD_MEMBER" -> boardService.editBoardMember(message.data)
        "DELETE_BOARD_MEMBER" -> boardService.deleteBoardMember(message.data)
        "EDIT_BOARD_WATCH" -> boardService.editBoardWatch(message.data)
        "ADD_BOARD_LABEL" -> boardService.addBoardLabel(message.data)
        "EDIT_BOARD_LABEL" -> boardService.editBoardLabel(message.data)
        "DELETE_BOARD_LABEL" -> boardService.deleteBoardLabel(message.data)
        "ADD_LIST" -> listService.addList(message.data)
        "EDIT_LIST" -> listService.editList(message.data)
        "MOVE_LIST" -> listService.moveList(message.data)
        "EDIT_LIST_ARCHIVE" -> listService.editListArchive(message.data)
        "ADD_CARD" -> cardService.addCard(message.data)
        "EDIT_CARD" -> cardService.editCard(message.data)
        "ARCHIVE_CARD" -> cardService.archiveCard(message.data)
        "DELETE_CARD" -> cardService.deleteCard(message.data)
        "MOVE_CARD" -> cardService.moveCard(message.data)
        "ADD_CARD_MEMBER" -> cardService.addCardMember(message.data)
        "DELETE_CARD_MEMBER" -> cardService.deleteCardMember(message.data)
        "ADD_CARD_LABEL" -> cardService.addCardLabel(message.data)
        "DELETE_CARD_LABEL" -> cardService.deleteCardLabel(message.data)
        "ADD_REPLY" -> replyService.addReply(message.data)
        "EDIT_REPLY" -> replyService.editReply(message.data)
        "DELETE_REPLY" -> replyService.deleteReply(message.data)
        "ADD_CARD_ATTACHMENT" -> attachmentService.addCardAttachment(message.data)
        "DELETE_CARD_ATTACHMENT" -> attachmentService.deleteCardAttachment(message.data)
        "EDIT_CARD_ATTACHMENT_COVER" -> attachmentService.editCardAttachmentCover(message.data)
        else -> {}
    }

    fun disconnect() {
        _job?.cancel()
        _job = null
    }
}