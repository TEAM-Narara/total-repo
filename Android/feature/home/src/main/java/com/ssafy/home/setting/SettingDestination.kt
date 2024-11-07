package com.ssafy.home.setting

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class Setting(val workspaceId: Long)

fun NavGraphBuilder.settingScreen(
    backHomeScreen: () -> Unit
) {
    composable<Setting> { backStackEntry: NavBackStackEntry ->
        val setting: Setting = backStackEntry.toRoute()

        HomeSettingScreen(
            workspaceId = setting.workspaceId,
            backHome = backHomeScreen
        )
    }
}
