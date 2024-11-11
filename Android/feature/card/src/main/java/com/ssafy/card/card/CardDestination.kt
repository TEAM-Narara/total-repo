package com.ssafy.card.card

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class Card(val id: Long)

fun NavGraphBuilder.cardScreen(
    popBackToBoardScreen: () -> Unit,
    moveToSelectLabel: (Long) -> Unit
) {
    composable<Card> { backStackEntry ->
        val card: Card = backStackEntry.toRoute()
        val id = card.id
        val viewModel: CardViewModel = hiltViewModel()

        viewModel.setCardId(id)

        CardScreen(
            popBackToBoardScreen = popBackToBoardScreen,
            moveToSelectLabel = { moveToSelectLabel(id) }
        )
    }
}
