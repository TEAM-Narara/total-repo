package com.ssafy.home.invite

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class InviteWorkspace(val workspaceId: Long)

fun NavGraphBuilder.inviteWorkspaceScreen(
    popBackToHome: () -> Unit
) {
    composable<InviteWorkspace> {
        val workspace: InviteWorkspace = it.toRoute()

        InviteWorkspaceScreen(
            popBackToHome = popBackToHome,
            workspaceId = workspace.workspaceId
        )
    }
}
