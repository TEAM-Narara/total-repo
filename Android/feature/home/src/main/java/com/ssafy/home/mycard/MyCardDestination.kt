package com.ssafy.home.mycard

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object MyCard

fun NavGraphBuilder.myCardScreen(
    popBackToHome: () -> Unit,
    moveToCardScreen: (boardId: Long, cardId: Long) -> Unit
) {
    composable<MyCard> {
        val viewModel = hiltViewModel<MyCardViewModel>().apply {
            getMyCard()
        }

        MyCardScreen(
            viewModel = viewModel,
            popBackToHome = popBackToHome,
            moveToCardScreen = moveToCardScreen
        )
    }
}
