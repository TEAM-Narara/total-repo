package com.ssafy.superboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ssafy.home.home.HOME
import com.ssafy.home.home.homeScreen
import com.ssafy.home.mycard.MY_CARD
import com.ssafy.home.mycard.myCardScreen
import com.ssafy.home.setting.SETTING
import com.ssafy.home.setting.settingScreen
import com.ssafy.login.login.LOGIN
import com.ssafy.login.login.loginScreen
import com.ssafy.login.signup.SIGN_UP
import com.ssafy.login.signup.signupScreen

@Composable
fun SuperBoardNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = HOME, modifier = modifier) {
        loginScreen(moveToSignUpScreen = {
            navController.navigate(SIGN_UP)
        })

        signupScreen(moveToLogInScreen = {
            navController.navigate(LOGIN)
        })

        homeScreen(
            moveToBoardScreen = {
                // TODO : navigate to board screen
            },
            moveToCreateNewBoardScreen = {
                // TODO : navigate to create new board screen
            },
            moveToLoginScreen = {
                navController.navigate(LOGIN) {
                    popUpTo(HOME) {
                        inclusive = true
                    }
                }
            },
            moveToSettingScreen = {
                navController.navigate(SETTING)
            },
            moveToMyCardScreen = {
                navController.navigate(MY_CARD)
            }
        )

        settingScreen(
            backHomeScreen = {
                navController.navigate(HOME) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        )

        myCardScreen(
            popUpToHome = {
                navController.popBackStack()
            },
            moveToCardScreen = {
                // TODO : navigate to card screen
            }
        )
    }
}
