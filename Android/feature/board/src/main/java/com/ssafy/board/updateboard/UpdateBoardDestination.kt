package com.ssafy.board.updateboard

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.board.Background
import kotlinx.serialization.Serializable

@Serializable
data class UpdateBoard(val boardId: Long)

fun NavGraphBuilder.updateBoardScreen(
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Background?) -> Unit
) {
    composable<UpdateBoard> { backStackEntry ->
        val updateBoard: UpdateBoard = backStackEntry.toRoute()
        val viewModel = hiltViewModel<UpdateBoardViewModel>().apply {
            setBoardId(updateBoard.boardId)
        }

        UpdateBoardScreen(
            viewModel = viewModel,
            popBackToHome = popBackToHome,
            moveToSelectBackgroundScreen = moveToSelectBackgroundScreen
        )
    }
}
