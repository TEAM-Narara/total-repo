package com.ssafy.home.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SearchWorkspace

fun NavGraphBuilder.searchWorkspaceScreen(
    onBackPressed: () -> Unit,
    moveToCardScreen: (Any) -> Unit
) {
    composable<SearchWorkspace> {
        SearchWorkspaceScreen(
            List(10) { Any() },
            onBackPressed,
            moveToCardScreen
        )
    }
}
