package com.ssafy.board.boardMenu

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.background.Cover
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
    moveToSelectBackGroundScreen: (Cover) -> Unit,
    moveToInviteMemberScreen: () -> Unit
) {
    composable<BoardMenu>(
        mapOf(typeOf<Cover?>() to coverType)
    ) { backStackEntry ->
        val boardSearch: BoardMenu = backStackEntry.toRoute()
        val boardId = boardSearch.boardId
        val workspaceId = boardSearch.workspaceId
        val background = boardSearch.cover
        val viewModel: BoardMenuViewModel = hiltViewModel<BoardMenuViewModel>().apply {
            setBoardId(boardId)
            setWorkspaceId(workspaceId)
        }

        BoardMenuScreen(
            viewModel = viewModel,
            backHome = popBack,
            cover = background ?: Cover(
                type = CoverType.COLOR,
                value = "#FFFFFF"
            ),
            moveToSelectBackGroundScreen = moveToSelectBackGroundScreen,
            moveToInviteMemberScreen = moveToInviteMemberScreen,
        )
    }
}
