package com.ssafy.board.boardMenu.data

import androidx.compose.runtime.Immutable
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.workspace.WorkSpaceDTO

@Immutable
data class BoardMenuData(
    val workSpaceDTO: WorkSpaceDTO,
    val boardDto: BoardDTO,
    val members: List<MemberResponseDTO>,
    val watchStatus: Boolean
)
