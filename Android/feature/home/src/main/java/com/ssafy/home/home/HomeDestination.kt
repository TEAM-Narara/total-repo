package com.ssafy.home.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val HOME = "HOME"

fun NavGraphBuilder.homeScreen(
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: () -> Unit,
    moveToLoginScreen: () -> Unit,
    moveToSettingScreen:()->Unit
) {
    composable(route = HOME) {
        HomeScreen(
            moveToBoardScreen = moveToBoardScreen,
            moveToCreateNewBoardScreen = moveToCreateNewBoardScreen,
            moveToLoginScreen = moveToLoginScreen,
            moveToSettingScreen= moveToSettingScreen
        )
    }
}
