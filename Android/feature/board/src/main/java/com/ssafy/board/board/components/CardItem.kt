package com.ssafy.board.board.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ReorderCardData
import com.ssafy.designsystem.component.CardItem
import com.ssafy.model.with.CoverType

@Composable
fun CardItem(modifier: Modifier = Modifier, cardData: ReorderCardData, onClick: () -> Unit = {}) =
    with(cardData.cardData) {
        CardItem(
            modifier = modifier,
            title = name,
            image = { Image() },
            labels = { },
            startTime = startAt,
            endTime = endAt,
            description = description != null,
            commentCount = cardReplies.size,
            manager = { /*TODO("Card Manager 구현 필요")*/ },
            onClick = onClick
        )
    }

@Composable
fun CardData.Image() = if (coverType != null && coverValue != null) {
    CardCover(coverType = coverType, coverValue = coverValue)
} else {
    null
}

@Composable
fun CardCover(modifier: Modifier = Modifier, coverType: CoverType, coverValue: String) {
    AsyncImage(
        model = coverValue,
        modifier = modifier.fillMaxSize(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
}
