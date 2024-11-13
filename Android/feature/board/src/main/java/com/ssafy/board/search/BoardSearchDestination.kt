package com.ssafy.board.search

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.search.SearchParameters
import com.ssafy.ui.safetype.SearchParametersType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class BoardSearch(
    val workspaceId: Long,
    val boardId: Long,
    val searchParameters: SearchParameters
)

fun NavGraphBuilder.boardSearchScreen(
    popBackToBoardScreenWithParams: (BoardSearch, SearchParameters) -> Unit,
    popBackToBoardScreen: () -> Unit
) {
    composable<BoardSearch>(
        mapOf(typeOf<SearchParameters>() to SearchParametersType),
    ) { backStackEntry ->
        val boardSearch: BoardSearch = backStackEntry.toRoute()
        val searchParameters = boardSearch.searchParameters
        val viewModel = hiltViewModel<BoardSearchViewModel>().apply {
            updateSearchParams(boardSearch.workspaceId, boardSearch.boardId, searchParameters)
        }

        BoardSearchScreen(
            viewModel = viewModel,
            popBackToBoardScreen = { popBackToBoardScreen() },
            popBackToBoardScreenWithParams = { newParams ->
                popBackToBoardScreenWithParams(boardSearch, newParams)
            }
        )
    }
}
