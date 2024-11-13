package com.ssafy.board.board

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.search.SearchParameters
import com.ssafy.ui.safetype.SearchParametersType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class Board(
    val workspaceId: Long,
    val boardId: Long,
)

fun NavGraphBuilder.boardScreen(
    popBack: () -> Unit,
    navigateToNotificationScreen: () -> Unit,
    navigateToBoardMenuScreen: (Long, Long) -> Unit,
    navigateToCardScreen: (Long, Long, Long) -> Unit
) {
    composable<Board>(
        mapOf(typeOf<SearchParameters>() to SearchParametersType)
    ) { backStackEntry ->
        val board: Board = backStackEntry.toRoute()
        val viewModel: BoardViewModel = hiltViewModel<BoardViewModel>().apply {
            setWorkspaceId(board.workspaceId)
            setBoardId(board.boardId)
        }
        BoardScreen(
            viewModel = viewModel,
            popBack = popBack,
            navigateToNotificationScreen = navigateToNotificationScreen,
            navigateToBoardMenuScreen = navigateToBoardMenuScreen,
            navigateToCardScreen = { cardId -> navigateToCardScreen(board.workspaceId, board.boardId, cardId) }
        )
    }
}
