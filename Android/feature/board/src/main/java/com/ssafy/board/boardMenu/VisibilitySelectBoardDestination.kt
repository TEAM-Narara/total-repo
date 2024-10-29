package com.ssafy.board.boardMenu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.background.BackgroundDto
import kotlinx.serialization.Serializable

@Serializable
data class Visibility(
    val selectedBackgroundColor: Long,
    val selectedBackgroundImg: String?,
)

fun NavGraphBuilder.visibilityBackgroundScreen(popBack: () -> Unit) {
    composable<Visibility> { backStackEntry ->
        val selectBackground: Visibility = backStackEntry.toRoute()
        val selectedBackgroundColor = selectBackground.selectedBackgroundColor
        val selectedBackgroundImg = selectBackground.selectedBackgroundImg


        SelectBoardBackground(
            onBackPressed = popBack,
            selectedBackground = BackgroundDto(
                color = selectedBackgroundColor,
                imgPath = selectedBackgroundImg
            )
        )
    }
}