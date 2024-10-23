package com.ssafy.superboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ssafy.home.createboard.CreateBoard
import com.ssafy.home.createboard.createBoard
import com.ssafy.home.home.Home
import com.ssafy.home.home.homeScreen
import com.ssafy.home.mycard.MyCard
import com.ssafy.home.mycard.myCardScreen
import com.ssafy.home.setting.Setting
import com.ssafy.home.setting.settingScreen
import com.ssafy.login.login.LogIn
import com.ssafy.login.login.loginScreen
import com.ssafy.login.signup.SignUp
import com.ssafy.login.signup.signupScreen

@Composable
fun SuperBoardNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Home, modifier = modifier) {
        loginScreen(moveToSignUpScreen = {
            navController.navigate(SignUp)
        })

        signupScreen(moveToLogInScreen = {
            navController.navigate(LogIn)
        })

        homeScreen(
            moveToBoardScreen = {
                // TODO : navigate to board screen
            },
            moveToCreateNewBoardScreen = {
                navController.navigate(CreateBoard(workspaceList = listOf("workspace1", "workspace2")))
            },
            moveToLoginScreen = {
                navController.navigate(LogIn) {
                    popUpTo(Home) {
                        inclusive = true
                    }
                }
            },
            moveToSettingScreen = {
                navController.navigate(Setting)
            },
            moveToMyCardScreen = {
                navController.navigate(MyCard)
            }
        )

        settingScreen(
            backHomeScreen = {
                navController.navigate(Home) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        )

        myCardScreen(
            popBackToHome = navController::popBackStack,
            moveToCardScreen = {
                // TODO : navigate to card screen
            }
        )

        createBoard(
            popBackToHome = navController::popBackStack,
            moveToSelectBackgroundScreen = {
                // TODO : navigate to select background screen
            }
        )
    }
}
