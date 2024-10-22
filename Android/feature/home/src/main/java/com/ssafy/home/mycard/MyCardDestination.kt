package com.ssafy.home.mycard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val MY_CARD = "MY_CARD"

fun NavGraphBuilder.myCardScreen(
    popUpToHome: () -> Unit,
    moveToCardScreen: (Any) -> Unit
) {
    composable(route = MY_CARD) {
        MyCardScreen(
            popUpToHome = popUpToHome,
            moveToCardScreen = moveToCardScreen
        )
    }
}
