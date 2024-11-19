package com.ssafy.board

import com.ssafy.data.socket.board.BoardStomp
import javax.inject.Inject

class DisconnectBoardStompUseCase @Inject constructor(
    private val boardStomp: BoardStomp
) {
    operator fun invoke() {
        boardStomp.disconnect()
    }
}