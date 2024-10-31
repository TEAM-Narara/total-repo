package com.ssafy.superboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ssafy.board.board.Board
import com.ssafy.board.board.boardScreen
import com.ssafy.board.boardMenu.BoardMenu
import com.ssafy.board.boardMenu.Visibility
import com.ssafy.board.boardMenu.boardMenuScreen
import com.ssafy.board.boardMenu.visibilityBackgroundScreen
import com.ssafy.board.member.boardInviteMemberDestination
import com.ssafy.board.search.BoardSearch
import com.ssafy.board.search.boardSearchScreen
import com.ssafy.board.updateboard.updateBoardScreen
import com.ssafy.card.card.Card
import com.ssafy.card.card.cardScreen
import com.ssafy.card.label.Label
import com.ssafy.card.label.labelScreen
import com.ssafy.home.createboard.CreateBoard
import com.ssafy.home.createboard.createBoardScreen
import com.ssafy.home.home.Home
import com.ssafy.home.home.homeScreen
import com.ssafy.home.member.workSpaceInviteMemberDestination
import com.ssafy.home.mycard.MyCard
import com.ssafy.home.mycard.myCardScreen
import com.ssafy.home.search.SearchWorkspace
import com.ssafy.home.search.searchWorkspaceScreen
import com.ssafy.home.setting.Setting
import com.ssafy.home.setting.settingScreen
import com.ssafy.home.update.UpdateProfile
import com.ssafy.home.update.updateProfileScreen
import com.ssafy.login.login.LogIn
import com.ssafy.login.login.loginScreen
import com.ssafy.login.signup.SignUp
import com.ssafy.login.signup.signupScreen
import com.ssafy.model.search.SearchParameters
import com.ssafy.notification.notification.notificationScreen

@Composable
fun SuperBoardNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Card(0L),
        modifier = modifier
    ) {
        loginScreen(
            moveToSignUpScreen = { navController.navigate(SignUp) },
            moveToHomeScreen = { navController.navigate(Home) }
        )

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

        updateBoardScreen(
            popBackToHome = navController::popBackStack,
            moveToSelectBackgroundScreen = {
                // TODO : navigate to select background screen
            }
        )

        boardMenuScreen(
            popBack = { navController.popBackStack() },
            setBackground = { selectedBackgroundColor: Long, selectBackgroundImg: String? ->
                navController.navigate(Visibility(selectedBackgroundColor, selectBackgroundImg))
            }
        )

        visibilityBackgroundScreen(popBack = { navController.popBackStack() })

        updateProfileScreen(backHomeScreen = { navController.popBackStack() })

        searchWorkspaceScreen(
            onBackPressed = { navController.popBackStack() },
            moveToCardScreen = {
                // TODO 리스트로 이동하고, 연속으로 카드로 이동시켜야함
            }
        )

        boardSearchScreen(
            popBackToBoardScreen = {
                navController.popBackStack()
            },

            popBackToBoardScreenWithParams = { boardSearch: BoardSearch, params: SearchParameters ->
                navController.navigate(BoardSearch(params)) {
                    popUpTo(boardSearch) {
                        inclusive = true
                    }
                }
            }
        )

        boardScreen(
            popBack = navController::popBackStack,
            navigateToFilterScreen = { searchParameters: SearchParameters ->
                navController.navigate(
                    BoardSearch(searchParameters)
                )
            },
            navigateToNotificationScreen = {},
            navigateToBoardMenuScreen = { boardId: Long, workspaceId: Long ->
                navController.navigate(
                    BoardMenu(boardId, workspaceId)
                )
            },
            navigateToCardScreen = { cardId: Long ->
                navController.navigate(Card(cardId))
            }
        )

        cardScreen(
            popBackToBoardScreen = {
                navController.popBackStack()
            },
            moveToSelectLabel = { cardId: Long ->
                navController.navigate(Label(cardId))
            }
        )

        notificationScreen(
            popBack = navController::popBackStack,
        )

        boardInviteMemberDestination(
            popBack = navController::popBackStack
        )

        workSpaceInviteMemberDestination(
            popBack = navController::popBackStack
        )

        labelScreen(
            popBack = navController::popBackStack
        )
    }
}
