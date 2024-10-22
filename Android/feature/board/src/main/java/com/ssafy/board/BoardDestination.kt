package com.ssafy.board

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val BOARD = "BOARD"

fun NavGraphBuilder.boardScreen(popBack: () -> Unit) {
    composable(route = BOARD) {
        BoardScreen(
            popBack = popBack
        )
    }
}
