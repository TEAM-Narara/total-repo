package com.ssafy.home.data

import androidx.compose.runtime.Immutable
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.user.User
import com.ssafy.model.workspace.WorkSpaceDTO

@Immutable
data class HomeData(
    val user: User = User("", "", null),
    val workspaceList: List<WorkSpaceDTO> = emptyList(),
    val selectedWorkSpace: SelectedWorkSpace = SelectedWorkSpace()
)

@Immutable
data class SelectedWorkSpace(
    val workSpaceId: Long = 0,
    val workSpaceName: String = "",
    val boards: List<BoardDTO> = emptyList()
)
