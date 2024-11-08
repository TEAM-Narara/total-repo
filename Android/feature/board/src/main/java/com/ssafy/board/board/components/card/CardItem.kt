package com.ssafy.board.board.components.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ssafy.board.board.data.ReorderCardData
import com.ssafy.designsystem.component.CardItem

@Composable
fun CardItem(modifier: Modifier = Modifier, cardData: ReorderCardData, onClick: () -> Unit = {}) =
    with(cardData.cardData) {
        CardItem(
            modifier = modifier,
            title = name,
            image = Cover,
            labels = Label,
            startTime = startAt,
            endTime = endAt,
            description = description != null,
            commentCount = cardReplies.size,
            manager = Manager,
            onClick = onClick
        )
    }