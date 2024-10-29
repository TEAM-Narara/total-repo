package com.ssafy.home.member

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class WorkSpaceInviteMember(val workspaceId: Long)

fun NavGraphBuilder.workSpaceInviteMemberDestination(
    popBack: () -> Unit,
) {
    composable<WorkSpaceInviteMember> { backStackEntry ->
        val workSpaceInviteMember: WorkSpaceInviteMember = backStackEntry.toRoute()
        val viewModel: InviteMemberViewModel = hiltViewModel<InviteMemberViewModel>().apply {
            setWorkspaceId(workSpaceInviteMember.workspaceId)
        }
        InviteMemberScreen(
            viewModel = viewModel,
            popBack = popBack
        )
    }
}
