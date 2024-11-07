package com.ssafy.board.boardMenu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.board.Background
import com.ssafy.ui.safetype.backgroundType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class SelectBackGround(val background: Background? = null)

fun NavGraphBuilder.selectBackgroundScreen(popBack: () -> Unit) {
    composable<SelectBackGround>(
        mapOf(typeOf<Background?>() to backgroundType)
    ) { backStackEntry ->
        val selectBackground: SelectBackGround = backStackEntry.toRoute()

        SelectBoardBackgroundScreen(
            onBackPressed = popBack,
            selectedBackground = BackgroundDto(
                color = selectedBackgroundColor,
                imgPath = selectedBackgroundImg ?: ""
            )
            selectedBackground = selectBackground.background
        )
    }
}