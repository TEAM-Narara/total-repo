package com.ssafy.board.boardMenu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.background.Cover
import com.ssafy.ui.safetype.coverType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class SelectBackGround(
    val cover: Cover? = null,
    val boardId: Long? = null
)

fun NavGraphBuilder.selectBackgroundScreen(popBack: (Cover?) -> Unit) {
    composable<SelectBackGround>(
        mapOf(typeOf<Cover?>() to coverType)
    ) { backStackEntry ->
        val selectBackground: SelectBackGround = backStackEntry.toRoute()

        SelectBoardBackgroundScreen(
            onBackPressed = popBack,
            selectedCover = selectBackground.cover,
            boardId = selectBackground.boardId
        )
    }
}