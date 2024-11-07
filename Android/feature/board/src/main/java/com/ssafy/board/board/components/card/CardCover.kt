package com.ssafy.board.board.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ssafy.board.board.data.CardData
import com.ssafy.designsystem.values.toColor
import com.ssafy.model.background.CoverType

val CardData.Cover: (@Composable () -> Unit)?
    get() = when (coverType) {
        CoverType.NONE -> null
        CoverType.COLOR -> coverValue?.let { { CardCoverImage(imgPath = it) } }
        CoverType.IMAGE -> coverValue?.toColor()?.let { { CardCoverColor(color = it) } }
    }

@Composable
fun CardCoverColor(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color)
    )
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