package com.ssafy.board.board.data

import com.ssafy.model.background.Background
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.with.ListInCardsDTO

data class BoardData(
    val id: Long,
    val workspaceId: Long,
    val name: String,
    val background: Background,
    val isClosed: Boolean,
    val visibility: Visibility,
    var listCollection: List<ListData>,
)

object BoardDataMapper {
    fun fromDto(board: BoardDTO, lists: List<ListInCardsDTO>): BoardData {
        return BoardData(
            id = board.id,
            workspaceId = board.workspaceId,
            name = board.name,
            background = board.background,
            isClosed = board.isClosed,
            visibility = board.visibility,
            listCollection = ListDataMapper.fromDto(lists)
        )
    }
}