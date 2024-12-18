package com.ssafy.superboard.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import com.ssafy.board.board.Board
import com.ssafy.board.board.boardScreen
import com.ssafy.board.boardMenu.BoardMenu
import com.ssafy.board.boardMenu.SelectBackGround
import com.ssafy.board.boardMenu.boardMenuScreen
import com.ssafy.board.boardMenu.selectBackgroundScreen
import com.ssafy.board.member.BoardInviteMember
import com.ssafy.board.member.boardInviteMemberDestination
import com.ssafy.card.card.Card
import com.ssafy.card.card.cardScreen
import com.ssafy.card.label.Label
import com.ssafy.card.label.labelScreen
import com.ssafy.home.createboard.CreateBoard
import com.ssafy.home.createboard.createBoardScreen
import com.ssafy.home.home.Home
import com.ssafy.home.home.homeScreen
import com.ssafy.home.invite.InviteWorkspace
import com.ssafy.home.invite.inviteWorkspaceScreen
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
import com.ssafy.model.background.Cover
import com.ssafy.model.manager.AuthManager
import com.ssafy.model.manager.ConnectManager
import com.ssafy.notification.Notification
import com.ssafy.notification.fcm.FcmEffect
import com.ssafy.notification.fcm.data.BoardMessage
import com.ssafy.notification.fcm.data.CardMessage
import com.ssafy.notification.fcm.data.HomeMessage
import com.ssafy.notification.fcm.data.WorkspaceMessage
import com.ssafy.notification.notificationScreen
import com.ssafy.splash.StartDirection
import com.ssafy.superboard.MainViewModel
import com.ssafy.ui.safetype.coverType
import com.ssafy.ui.uistate.ErrorScreen

@Composable
fun SuperBoardNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: MainViewModel,
    direction: StartDirection = StartDirection.LOGIN,
) {
    val authEvent by viewModel.authEvent.collectAsStateWithLifecycle(false)
    val connectingEvent by ConnectManager.connectingEvent.collectAsStateWithLifecycle(false)

    NavHost(
        navController = navController,
        startDestination = if (direction == StartDirection.LOGIN) LogIn else Home(),
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {

        loginScreen(
            moveToSignUpScreen = { navController.navigate(SignUp) },
            moveToHomeScreen = { navController.navigate(Home()) { popUpAll(navController) } }
        )

        signupScreen(

            moveToHomeScreen = { navController.navigate(Home()) }
        )

        homeScreen(
            moveToBoardScreen = { workspaceId, boardId ->
                navController.navigate(Board(workspaceId, boardId))
            },
            moveToCreateNewBoardScreen = {
                navController.navigate(CreateBoard())
            },
            moveToLoginScreen = {
                navController.navigate(LogIn) { popUpAll(navController) }
            },
            moveToSettingScreen = { workspaceId: Long ->
                navController.navigate(Setting(workspaceId))
            },
            moveToMyCardScreen = {
                navController.navigate(MyCard)
            },
            moveToUpdateProfile = {
                navController.navigate(UpdateProfile)
            },
            moveToSearchScreen = {
                navController.navigate(SearchWorkspace)
            },
            moveToAlarmScreen = {
                navController.navigate(Notification)
            }
        )

        settingScreen(
            backHomeScreen = {
                navController.popBackStack()
            },
            moveToInviteWorkspace = { workspaceId: Long ->
                navController.navigate(InviteWorkspace(workspaceId))
            }
        )

        myCardScreen(
            popBackToHome = navController::popBackStack,
            moveToCardScreen = { workspaceId: Long, boardId: Long, cardId: Long ->
                navController.navigate(Board(workspaceId, boardId))
                navController.navigate(Card(workspaceId, boardId, cardId))
            }
        )

        createBoardScreen(
            popBackToHome = navController::popBackStack,
            moveToSelectBackgroundScreen = { cover: Cover? ->
                navController.navigate(SelectBackGround(cover))
            }
        )

        inviteWorkspaceScreen(
            popBackToHome = navController::popBackStack
        )

        // TODO : 현재는 보드 설정 화면에서 기능을 모두 수행합니다.
//        updateBoardScreen(
//            popBackToHome = navController::popBackStack,
//            moveToSelectBackgroundScreen = { cover: Cover? ->
//                navController.navigate(SelectBackGround(cover))
//            }
//        )

        boardMenuScreen(
            popBack = { navController.popBackStack() },
            moveToSelectBackGroundScreen = { cover: Cover?, boardId: Long? ->
                navController.navigate(SelectBackGround(cover, boardId))
            },
            moveToInviteMemberScreen = { boardId: Long ->
                navController.navigate(BoardInviteMember(boardId))
            }
        )

        selectBackgroundScreen(
            popBack = { newCover: Cover? ->
                val jsonCover = coverType.serializeAsValue(newCover)
                navController.previousBackStackEntry?.savedStateHandle?.set(Cover.KEY, jsonCover)
                navController.popBackStack()
            }
        )

        updateProfileScreen(backHomeScreen = { navController.popBackStack() })

        searchWorkspaceScreen(
            onBackPressed = { navController.popBackStack() },
            moveToCardScreen = {
                // TODO 리스트로 이동하고, 연속으로 카드로 이동시켜야함
            }
        )

        boardScreen(
            popBack = navController::popBackStack,
            navigateToNotificationScreen = {
                navController.navigate(Notification)
            },
            navigateToBoardMenuScreen = { boardId: Long, workspaceId: Long ->
                navController.navigate(
                    BoardMenu(boardId, workspaceId)
                )
            },
            navigateToCardScreen = { workspaceId: Long, boardId: Long, cardId: Long ->
                navController.navigate(Card(workspaceId, boardId, cardId))
            }
        )

        cardScreen(
            popBackToBoardScreen = {
                navController.popBackStack()
            },
            moveToSelectLabel = { boardId: Long, cardId: Long ->
                navController.navigate(Label(boardId, cardId))
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

    FcmEffect { fcmDirection ->
        when (fcmDirection) {
            is HomeMessage -> {
                navController.navigate(Home()) { popUpAll(navController) }
            }

            is WorkspaceMessage -> {
                with(fcmDirection) {
                    navController.navigate(Home(workspaceId)) { popUpAll(navController) }
                }
            }

            is BoardMessage -> {
                with(fcmDirection) {
                    navController.navigate(Home(workspaceId)) { popUpAll(navController) }
                    navController.navigate(Board(workspaceId, boardId))
                }
            }

            is CardMessage -> {
                with(fcmDirection) {
                    navController.navigate(Home(workspaceId)) { popUpAll(navController) }
                    navController.navigate(Board(workspaceId, boardId))
                    navController.navigate(Card(workspaceId, boardId, cardId))
                }
            }
        }
    }

    if (authEvent) {
        navController.navigate(LogIn) { popUpAll(navController) }
        ErrorScreen(errorMessage = AuthManager.NO_AUTH)
    }
}

private fun NavOptionsBuilder.popUpAll(navController: NavController) {
    popUpTo(navController.graph.id) {
        inclusive = true
    }
}
