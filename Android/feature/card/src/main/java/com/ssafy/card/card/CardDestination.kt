package com.ssafy.card.card

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class Card(val workspaceId: Long, val boardId: Long, val cardId: Long)

fun NavGraphBuilder.cardScreen(
    popBackToBoardScreen: () -> Unit,
    moveToSelectLabel: (Long, Long) -> Unit
) {
    composable<Card> { backStackEntry ->
        val card: Card = backStackEntry.toRoute()
        val viewModel: CardViewModel = hiltViewModel()

        viewModel.setWorkspaceId(card.workspaceId)
        viewModel.setBardId(card.boardId)
        viewModel.setCardId(card.cardId)

        CardScreen(
            popBackToBoardScreen = popBackToBoardScreen,
            moveToSelectLabel = { moveToSelectLabel(card.boardId, card.cardId) }
        )
    }
}
