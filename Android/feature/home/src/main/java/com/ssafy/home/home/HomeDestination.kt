package com.ssafy.home.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.serialization.Serializable

@Serializable
object Home

fun NavGraphBuilder.homeScreen(
    moveToBoardScreen: (Long, Long) -> Unit,
    moveToCreateNewBoardScreen: (List<WorkSpaceDTO>) -> Unit,
    moveToLoginScreen: () -> Unit,
    moveToSettingScreen: (Long) -> Unit,
    moveToMyCardScreen: () -> Unit,
    moveToUpdateProfile: () -> Unit,
    moveToSearchScreen: () -> Unit,
    moveToAlarmScreen: () -> Unit
) {
    composable<Home> {

        HomeScreen(
            moveToBoardScreen = moveToBoardScreen,
            moveToCreateNewBoardScreen = moveToCreateNewBoardScreen,
            moveToLoginScreen = moveToLoginScreen,
            moveToSettingScreen = moveToSettingScreen,
            moveToMyCardScreen = moveToMyCardScreen,
            moveToUpdateProfile = moveToUpdateProfile,
            moveToSearchScreen = moveToSearchScreen,
            moveToAlarmScreen = moveToAlarmScreen
        )
    }
}
