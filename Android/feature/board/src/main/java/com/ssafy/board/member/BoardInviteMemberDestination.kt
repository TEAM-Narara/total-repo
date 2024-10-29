package com.ssafy.board.member

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.board.board.BoardViewModel
import kotlinx.serialization.Serializable

@Serializable
data class BoardInviteMember(val boardId: Long)

fun NavGraphBuilder.boardInviteMemberDestination(
    popBack: () -> Unit,
) {
    composable<BoardInviteMember> { backStackEntry ->
        val boardInviteMember: BoardInviteMember = backStackEntry.toRoute()
        val viewModel: InviteMemberViewModel = hiltViewModel<InviteMemberViewModel>().apply {
            setBoardId(boardInviteMember.boardId)
        }
        InviteMemberScreen(
            viewModel = viewModel,
            popBack = popBack
        )
    }
}
