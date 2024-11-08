package com.ssafy.board.boardMenu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.background.Cover
import com.ssafy.model.card.HistoryData
import com.ssafy.model.with.CoverType
import com.ssafy.ui.safetype.coverType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class BoardMenu(
    val boardId: Long,
    val workspaceId: Long,
    val cover: Cover? = null
)

fun NavGraphBuilder.boardMenuScreen(
    popBack: () -> Unit,
    selectBackGroundScreen: (Cover) -> Unit
) {
    composable<BoardMenu>(
        mapOf(typeOf<Cover?>() to coverType)
    ) { backStackEntry ->
        val boardSearch: BoardMenu = backStackEntry.toRoute()
        val boardId = boardSearch.boardId
        val workspaceId = boardSearch.workspaceId
        val background = boardSearch.cover

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
            cover = background ?: Cover(
                type = CoverType.COLOR,
                value = "#FFFFFF"
            ),
            selectBackGroundScreen = selectBackGroundScreen,
        )
    }
}
