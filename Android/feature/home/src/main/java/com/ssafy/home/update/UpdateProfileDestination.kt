package com.ssafy.home.update

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object UpdateProfile

fun NavGraphBuilder.updateProfileScreen(
    backHomeScreen: () -> Unit
) {
    composable<UpdateProfile> {
        UpdateProfileScreen(onBackPressed = { backHomeScreen() })
    }
}
