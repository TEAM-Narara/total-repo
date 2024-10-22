package com.ssafy.board.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ssafy.board.data.CardData
import com.ssafy.designsystem.component.CardItem

@Composable
fun CardItem(modifier: Modifier = Modifier, cardData: CardData) {
    CardItem(modifier = modifier, title = cardData.title)
}