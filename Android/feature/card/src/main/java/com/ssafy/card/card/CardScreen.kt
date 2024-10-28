package com.ssafy.card.card

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CardScreen(
    viewModel: CardViewModel = hiltViewModel(),
    popBackToBoardScreen: () -> Unit,
    moveToSelectColor: () -> Unit
) {

}