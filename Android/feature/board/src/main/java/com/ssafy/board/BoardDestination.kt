package com.ssafy.board

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Board

fun NavGraphBuilder.boardScreen(popBack: () -> Unit, moveBoardSetting: () -> Unit) {
    composable<Board> {
        BoardScreen(
            popBack = popBack,
            moveBoardSetting = moveBoardSetting
        )
    }
}
