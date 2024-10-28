package com.ssafy.superboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ssafy.board.Board
import com.ssafy.board.boardScreen
import com.ssafy.board.search.BoardSearch
import com.ssafy.board.search.boardSearchScreen
import com.ssafy.home.createboard.CreateBoard
import com.ssafy.home.createboard.createBoardScreen
import com.ssafy.home.home.Home
import com.ssafy.home.home.homeScreen
import com.ssafy.home.mycard.MyCard
import com.ssafy.home.mycard.myCardScreen
import com.ssafy.home.search.SearchWorkspace
import com.ssafy.home.search.searchWorkspaceScreen
import com.ssafy.home.setting.Setting
import com.ssafy.home.setting.settingScreen
import com.ssafy.login.login.LogIn
import com.ssafy.login.login.loginScreen
import com.ssafy.login.signup.SignUp
import com.ssafy.login.signup.signupScreen
import com.ssafy.model.search.SearchParameters
import com.ssafy.home.update.UpdateProfile
import com.ssafy.home.update.updateProfileScreen

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
                navController.navigate(Board(it))
            },
            moveToCreateNewBoardScreen = {
                navController.navigate(
                    CreateBoard(
                        workspaceList = listOf(
                            "workspace1",
                            "workspace2"
                        )
                    )
                )
            },
            moveToLoginScreen = {
                navController.navigate(LogIn) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
            moveToSettingScreen = {
                navController.navigate(Setting)
            },
            moveToMyCardScreen = {
                navController.navigate(MyCard)
            },
            moveToUpdateProfile = {
                navController.navigate(UpdateProfile)
            },
            moveToSearchScreen = {
                navController.navigate(SearchWorkspace)
            }
        )

        settingScreen(
            backHomeScreen = {
                navController.navigate(Home) {
                    popUpTo(navController.graph.id) {
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

        createBoardScreen(
            popBackToHome = navController::popBackStack,
            moveToSelectBackgroundScreen = {
                // TODO : navigate to select background screen
            }
        )

        updateProfileScreen(backHomeScreen = { navController.popBackStack() })

        searchWorkspaceScreen(
            onBackPressed = { navController.popBackStack() },
            moveToCardScreen = {}
        )

        boardScreen(
            popBack = {
                navController.popBackStack()
            },
            moveToBoardSearch = { searchParameters: SearchParameters ->
                navController.navigate(BoardSearch(searchParameters))
            }
        )

        boardSearchScreen(
            popBackToBoardScreen = {
                navController.popBackStack()
            },

            popBackToBoardScreenWithParams = { boardSearch: BoardSearch, params: SearchParameters ->
                navController.navigate(Board(params)) {
                    popUpTo(boardSearch) {
                        inclusive = true
                    }
                }
            }
        )
    }
}
