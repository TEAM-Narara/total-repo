package com.ssafy.home.data

import androidx.compose.runtime.Immutable
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.user.User
import com.ssafy.model.workspace.WorkSpaceDTO

@Immutable
data class HomeData(
    val user: User = User("", "", null),
    val workspaceList: List<WorkSpaceDTO> = emptyList(),
    val boardsBySelectedWorkSpace: List<BoardDTO> = emptyList()
)
