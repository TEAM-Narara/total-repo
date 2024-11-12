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
    val boardId: Long,
    val searchParameters: SearchParameters = SearchParameters()
)

fun NavGraphBuilder.boardScreen(
    popBack: () -> Unit,
    navigateToFilterScreen: (SearchParameters) -> Unit,
    navigateToNotificationScreen: () -> Unit,
    navigateToBoardMenuScreen: (Long, Long) -> Unit,
    navigateToCardScreen: (Long, Long) -> Unit
) {
    composable<Board>(
        mapOf(typeOf<SearchParameters>() to SearchParametersType)
    ) { backStackEntry ->
        val board: Board = backStackEntry.toRoute()
        val viewModel: BoardViewModel = hiltViewModel<BoardViewModel>().apply {
            setBoardId(board.boardId)
        }
        BoardScreen(
            viewModel = viewModel,
            popBack = popBack,
            searchParameters = board.searchParameters,
            navigateToFilterScreen = navigateToFilterScreen,
            navigateToNotificationScreen = navigateToNotificationScreen,
            navigateToBoardMenuScreen = navigateToBoardMenuScreen,
            navigateToCardScreen = { cardId -> navigateToCardScreen(board.boardId, cardId) }
        )
    }
}
