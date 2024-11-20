package com.ssafy.login.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SignUp

fun NavGraphBuilder.signupScreen(
    moveToHomeScreen: () -> Unit
) {
    composable<SignUp> {
        SignUpScreen(
            moveToHomeScreen = moveToHomeScreen
        )
    }
}
