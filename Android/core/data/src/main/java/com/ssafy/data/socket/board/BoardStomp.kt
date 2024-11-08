package com.ssafy.data.socket.board

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.socket.BaseStompManager
import com.ssafy.data.socket.board.service.BoardService
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
                    "EDIT_BOARD_ARCHIVE" -> boardService.editBoardArchive(it.data)
                    "EDIT_BOARD_WATCH" -> boardService.editBoardWatch(it.data)
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