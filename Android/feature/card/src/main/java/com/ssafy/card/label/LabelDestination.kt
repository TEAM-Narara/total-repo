package com.ssafy.card.label

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class Label(val boardId: Long, val cardId: Long)

fun NavGraphBuilder.labelScreen(
    popBack: () -> Unit
) {
    composable<Label> { backStackEntry ->
        val label: Label = backStackEntry.toRoute()
        val viewModel = hiltViewModel<LabelViewModel>().apply {
            setBoardId(label.boardId)
            setCardId(label.cardId)
        }
        LabelScreen(
            viewModel = viewModel,
            popBack = popBack
        )
    }
}