package com.ssafy.home.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.serialization.Serializable

@Serializable
data class Home(val selectedWorkspaceId: Long? = null)

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
    composable<Home> { backstackEntry ->
        val home: Home = backstackEntry.toRoute()
        val viewModel = hiltViewModel<HomeViewModel>().apply {
            home.selectedWorkspaceId?.let { updateSelectedWorkspace(it) }
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
