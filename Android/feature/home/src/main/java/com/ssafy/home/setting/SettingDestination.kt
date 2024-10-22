package com.ssafy.home.setting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SETTING = "SETTING"

fun NavGraphBuilder.settingScreen(
    backHomeScreen: () -> Unit
) {
    composable(route = SETTING) {
        HomeSettingScreen(
            workspaceId = 0,
            backHome = backHomeScreen
        )
    }
}
