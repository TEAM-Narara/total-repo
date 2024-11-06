package com.ssafy.home.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.serialization.Serializable

@Serializable
object Home

fun NavGraphBuilder.homeScreen(
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: (List<WorkSpaceDTO>) -> Unit,
    moveToLoginScreen: () -> Unit,
    moveToSettingScreen: () -> Unit,
    moveToMyCardScreen: () -> Unit,
    moveToUpdateProfile: () -> Unit,
    moveToSearchScreen: () -> Unit,
    moveToAlarmScreen: () -> Unit
) {
    composable<Home> {
        val viewModel: HomeViewModel = hiltViewModel<HomeViewModel>().apply {
            getHomeInfo()
        }

        HomeScreen(
            viewModel = viewModel,
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
