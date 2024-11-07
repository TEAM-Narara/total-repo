package com.ssafy.board.boardMenu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.board.Background
import com.ssafy.model.board.BackgroundType
import com.ssafy.model.card.HistoryData
import com.ssafy.ui.safetype.backgroundType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class BoardMenu(
    val boardId: Long,
    val workspaceId: Long,
    val background: Background? = null
)

fun NavGraphBuilder.boardMenuScreen(
    popBack: () -> Unit,
    selectBackGroundScreen: (Background) -> Unit
) {
    composable<BoardMenu>(
        mapOf(typeOf<Background?>() to backgroundType)
    ) { backStackEntry ->
        val boardSearch: BoardMenu = backStackEntry.toRoute()
        val boardId = boardSearch.boardId
        val workspaceId = boardSearch.workspaceId
        val background = boardSearch.background

        BoardMenuScreen(
            boardId = boardId,
            backHome = popBack,
            workspaceId = workspaceId,
            historyContent = List(8) {//TODO: 추후 삭제, viewmodel에서 받아오도록 함.
                HistoryData(
                    "rename",
                    "손오공 renamed test(from testboard)",
                    300
                )
            },
            background = background ?: Background(
                type = BackgroundType.COLOR,
                value = "#FFFFFF"
            ),
            selectBackGroundScreen = selectBackGroundScreen,
        )
    }
}
