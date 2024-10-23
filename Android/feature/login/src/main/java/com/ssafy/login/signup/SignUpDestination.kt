package com.ssafy.login.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SignUp

fun NavGraphBuilder.signupScreen(moveToLogInScreen: () -> Unit) {
    composable<SignUp> {
        SignUpScreen(moveToLogInScreen = moveToLogInScreen)
    }
}
