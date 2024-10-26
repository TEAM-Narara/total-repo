package com.ssafy.board

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.search.SearchParameters
import com.ssafy.ui.safetype.SearchParametersType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class Board(
    val searchParameters: SearchParameters = SearchParameters()
)

fun NavGraphBuilder.boardScreen(
    popBack: () -> Unit,
    moveBoardSetting: () -> Unit,
    moveToBoardSearch: (SearchParameters) -> Unit
) {
    composable<Board>(
        mapOf(typeOf<SearchParameters>() to SearchParametersType)
    ) { backStackEntry ->
        val board: Board = backStackEntry.toRoute()
        val searchParameters = board.searchParameters

        BoardScreen(
            popBack = popBack,
            moveToBoardSearch = moveToBoardSearch,
            moveBoardSetting = moveBoardSetting,
            searchParameters = searchParameters,
        )
    }
}
