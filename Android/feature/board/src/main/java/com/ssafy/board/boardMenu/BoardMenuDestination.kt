package com.ssafy.board.boardMenu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.board.data.HistoryData
import kotlinx.serialization.Serializable

@Serializable
object BoardMenu

fun NavGraphBuilder.boardMenuScreen(popBack: () -> Unit) {
    composable<BoardMenu> {
        BoardMenuScreen(
            boardId = 1,
            backHome = popBack,
            workspaceId = 1,
            historyContent = List<HistoryData>(8) {
                HistoryData(
                    "rename",
                    "손오공 renamed test(from testboard)",
                    300
                )
            }
        )
    }
}
