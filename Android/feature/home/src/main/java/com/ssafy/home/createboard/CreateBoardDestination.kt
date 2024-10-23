package com.ssafy.home.createboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class CreateBoard(
    // TODO : WorkSpaceList에 대한 DTO 변경 필요
    val workspaceList: List<String>,
)

fun NavGraphBuilder.createBoard(
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: () -> Unit
) {
    composable<CreateBoard> { backStackEntry ->
        val createBoard: CreateBoard = backStackEntry.toRoute()

        CreateBoard(
            workspaceList = createBoard.workspaceList,
            popBackToHome = popBackToHome,
            moveToSelectBackgroundScreen = moveToSelectBackgroundScreen
        )
    }
}
