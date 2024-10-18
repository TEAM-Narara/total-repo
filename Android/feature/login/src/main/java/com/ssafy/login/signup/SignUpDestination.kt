package com.ssafy.login.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SIGN_UP = "SIGN_UP"

fun NavGraphBuilder.signupScreen(moveToLogInScreen: () -> Unit) {
    composable(route = SIGN_UP) {
        SignUpScreen(moveToLogInScreen = moveToLogInScreen)
    }
}
