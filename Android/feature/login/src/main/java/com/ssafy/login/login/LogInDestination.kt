package com.ssafy.login.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object LogIn

fun NavGraphBuilder.loginScreen(
    moveToSignUpScreen: () -> Unit,
    moveToHomeScreen: () -> Unit
) {
    composable<LogIn> {
        LogInScreen(
            moveToSignUpScreen = moveToSignUpScreen,
            moveToHomeScreen = moveToHomeScreen
        )
    }
}
