package com.ssafy.home.mycard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object MyCard

fun NavGraphBuilder.myCardScreen(
    popBackToHome: () -> Unit,
    moveToCardScreen: (Any) -> Unit
) {
    composable<MyCard> {
        MyCardScreen(
            popBackToHome = popBackToHome,
            moveToCardScreen = moveToCardScreen
        )
    }
}
