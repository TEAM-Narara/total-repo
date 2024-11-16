package com.ssafy.board.board.data

import com.ssafy.model.background.Cover
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.with.ListInCard

data class BoardData(
    val id: Long,
    val workspaceId: Long,
    val name: String,
    val cover: Cover,
    val isClosed: Boolean,
    val visibility: Visibility,
    var listCollection: List<ListData>,
)

object BoardDataMapper {
    fun fromDto(board: BoardDTO, lists: List<ListInCard>): BoardData {
        return BoardData(
            id = board.id,
            workspaceId = board.workspaceId,
            name = board.name,
            cover = board.cover,
            isClosed = board.isClosed,
            visibility = board.visibility,
            listCollection = ListDataMapper.fromDto(lists)
        )
    }
}