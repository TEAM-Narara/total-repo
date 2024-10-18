package com.ssafy.superboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ssafy.login.login.LOGIN
import com.ssafy.login.login.loginScreen
import com.ssafy.login.signup.SIGN_UP
import com.ssafy.login.signup.signupScreen

@Composable
fun SuperBoardNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = LOGIN, modifier = modifier) {
        loginScreen(moveToSignUpScreen = {
            navController.navigate(SIGN_UP)
        })

        signupScreen(moveToLogInScreen = {
            navController.navigate(LOGIN)
        })
    }
}
