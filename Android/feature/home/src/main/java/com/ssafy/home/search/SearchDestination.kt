package com.ssafy.home.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Search

fun NavGraphBuilder.searchScreen(
    onBackPressed: () -> Unit,
    moveToCardScreen: (Any) -> Unit
) {
    composable<Search> {
        SearchScreen(
            List(10) { Any() },
            onBackPressed,
            moveToCardScreen
        )
    }
}
