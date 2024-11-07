package com.ssafy.board.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            labels = { /*TODO("Card Label 구현 필요")*/ },
            startTime = startAt,
            endTime = endAt,
            description = description != null,
            commentCount = cardReplies.size,
            manager = { /*TODO("Card Manager 구현 필요")*/ },
            onClick = onClick
        )
    }

@Composable
fun CardData.Image() = when (coverType) {
    CoverType.NONE -> null
    CoverType.COLOR -> coverValue?.let { CardCoverImage(imgPath = it) }
    CoverType.IMAGE -> coverValue?.let { CardCoverColor(color = Color(0)) } // TODO : coverValue to Color 추가하기
}

@Composable
fun CardCoverColor(modifier: Modifier = Modifier, color: Color) {
    Box(modifier = modifier.fillMaxSize().background(color))
}

@Composable
fun CardCoverImage(modifier: Modifier = Modifier, imgPath: String) {
    AsyncImage(
        model = imgPath,
        modifier = modifier.fillMaxSize(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
}