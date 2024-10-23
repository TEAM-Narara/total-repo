package com.ssafy.home.setting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Setting

fun NavGraphBuilder.settingScreen(
    backHomeScreen: () -> Unit
) {
    composable<Setting> {
        HomeSettingScreen(
            workspaceId = 0,
            backHome = backHomeScreen
        )
    }
}
