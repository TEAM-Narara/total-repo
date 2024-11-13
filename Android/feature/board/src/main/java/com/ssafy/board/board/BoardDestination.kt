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
    val searchParameters: SearchParameters = SearchParameters()
)

fun NavGraphBuilder.boardScreen(
    popBack: () -> Unit,
    navigateToFilterScreen: (Long, Long, SearchParameters) -> Unit,
    navigateToNotificationScreen: () -> Unit,
    navigateToBoardMenuScreen: (Long, Long) -> Unit,
    navigateToCardScreen: (Long, Long, Long) -> Unit
) {
    composable<Board>(
        mapOf(typeOf<SearchParameters>() to SearchParametersType)
    ) { backStackEntry ->
        val board: Board = backStackEntry.toRoute()
        val viewModel: BoardViewModel = hiltViewModel<BoardViewModel>().apply {
            setBoardId(board.boardId)
            setSearchParams(board.searchParameters)
        }
        BoardScreen(
            viewModel = viewModel,
            popBack = popBack,
            navigateToFilterScreen = { navigateToFilterScreen(board.workspaceId, board.boardId, it) },
            navigateToNotificationScreen = navigateToNotificationScreen,
            navigateToBoardMenuScreen = navigateToBoardMenuScreen,
            navigateToCardScreen = { cardId -> navigateToCardScreen(board.workspaceId, board.boardId, cardId) }
        )
    }
}
