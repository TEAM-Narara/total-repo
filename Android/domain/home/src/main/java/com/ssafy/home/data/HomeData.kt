package com.ssafy.home.data

import androidx.compose.runtime.Immutable
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.user.User
import com.ssafy.model.workspace.WorkSpaceDTO

@Immutable
data class HomeData(
    val user: User = User(0L,"", "", null),
    val workspaceList: List<WorkSpaceDTO> = emptyList(),
    val selectedWorkSpace: SelectedWorkSpace = SelectedWorkSpace()
)

@Immutable
data class SelectedWorkSpace(
    val workspaceId: Long = 0,
    val workspaceName: String = "",
    val boards: List<BoardDTO> = emptyList()
)
