package com.ssafy.board.boardMenu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.card.HistoryData
import kotlinx.serialization.Serializable

@Serializable
data class BoardMenu(
    val boardId: Long,
    val workspaceId: Long,
)

fun NavGraphBuilder.boardMenuScreen(
    popBack: () -> Unit,
    setBackground: (Long, String?) -> Unit
) {
    composable<BoardMenu> { backStackEntry ->
        val boardSearch: BoardMenu = backStackEntry.toRoute()
        val boardId = boardSearch.boardId
        val workspaceId = boardSearch.workspaceId

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
            setBackground = setBackground
        )
    }
}
