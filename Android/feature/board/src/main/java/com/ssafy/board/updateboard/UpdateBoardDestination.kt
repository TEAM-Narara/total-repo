package com.ssafy.board.updateboard

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.background.Cover
import kotlinx.serialization.Serializable

@Serializable
data class UpdateBoard(val boardId: Long)

fun NavGraphBuilder.updateBoardScreen(
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Cover?) -> Unit
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
