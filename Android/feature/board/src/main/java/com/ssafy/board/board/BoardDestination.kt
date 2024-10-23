package com.ssafy.board.board

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class Board(val boardId: Long)

fun NavGraphBuilder.boardScreen(
    popBack: () -> Unit,
    navigateToFilterScreen: () -> Unit,
    navigateToNotificationScreen: () -> Unit,
    navigateToBoardMenuScreen: () -> Unit,
) {
    composable<Board> { backStackEntry ->
        val board: Board = backStackEntry.toRoute()
        val viewModel: BoardViewModel = hiltViewModel<BoardViewModel>().apply {
            setBoardId(board.boardId)
        }
        BoardScreen(
            viewModel = viewModel,
            popBack = popBack,
            navigateToFilterScreen = navigateToFilterScreen,
            navigateToNotificationScreen = navigateToNotificationScreen,
            navigateToBoardMenuScreen = navigateToBoardMenuScreen,
        )
    }
}
