package com.ssafy.board.boardMenu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.background.BackgroundDto
import kotlinx.serialization.Serializable

@Serializable
data class Visibility(
    val colors: List<Long>,
    val selectedBackgroundColor: Long,
    val selectedBackgroundImg: String?,
)

fun NavGraphBuilder.visibilityBackgroundScreen(popBack: () -> Unit) {
    composable<Visibility> { backStackEntry ->
        val selectBackground: Visibility = backStackEntry.toRoute()
        val colors = selectBackground.colors
        val selectedBackgroundColor = selectBackground.selectedBackgroundColor
        val selectedBackgroundImg = selectBackground.selectedBackgroundImg


        SelectBoardBackground(
            onBackPressed = popBack,
            colors = colors,
            selectedBackground = BackgroundDto(
                color = selectedBackgroundColor,
                imgPath = selectedBackgroundImg
            )
        )
    }
}