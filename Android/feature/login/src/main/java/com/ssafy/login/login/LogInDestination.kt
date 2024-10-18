package com.ssafy.login.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val LOGIN = "LOG_IN"

fun NavGraphBuilder.loginScreen(moveToSignUpScreen: () -> Unit) {
    composable(route = LOGIN) {
        LogInScreen(moveToSignUpScreen = moveToSignUpScreen)
    }
}
