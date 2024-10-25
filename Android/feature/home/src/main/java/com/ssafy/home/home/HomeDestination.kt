package com.ssafy.home.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Home

fun NavGraphBuilder.homeScreen(
    moveToBoardScreen: (Long) -> Unit,
    // TODO : WorkSpaceList에 대한 DTO 변경 필요
    moveToCreateNewBoardScreen: (List<String>) -> Unit,
    moveToLoginScreen: () -> Unit,
    moveToSettingScreen: () -> Unit,
    moveToMyCardScreen: () -> Unit,
    moveToUpdateProfile: () -> Unit,
    moveToSearchScreen: () -> Unit
) {
    composable<Home> {
        HomeScreen(
            moveToBoardScreen = moveToBoardScreen,
            moveToCreateNewBoardScreen = moveToCreateNewBoardScreen,
            moveToLoginScreen = moveToLoginScreen,
            moveToSettingScreen = moveToSettingScreen,
            moveToMyCardScreen = moveToMyCardScreen,
            moveToUpdateProfile = moveToUpdateProfile,
            moveToSearchScreen = moveToSearchScreen
        )
    }
}
