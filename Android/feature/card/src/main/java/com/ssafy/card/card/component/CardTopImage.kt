package com.ssafy.card.card.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import coil3.compose.AsyncImage
import com.ssafy.designsystem.values.IconLegendLarge
import com.ssafy.model.background.Cover

@Composable
fun CardTopImage(
    modifier: Modifier = Modifier,
    cover: Cover,
    heightOffset: Float
) {
    val height by animateDpAsState(
        targetValue = with(LocalDensity.current) {
            IconLegendLarge + heightOffset.toDp()
        },
        label = "height"
    )

    if (cover.value == "IMAGE") {
        AsyncImage(
            model = cover.value,
            contentDescription = null,
            modifier = modifier.height(height),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .height(height)
                .fillMaxWidth()
                .background(color = Color.LightGray)
        )
    }
}
