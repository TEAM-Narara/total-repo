package com.ssafy.board.board.components.card

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ssafy.board.board.data.CardData

val CardData.Manager: (@Composable RowScope.() -> Unit)?
    get() = if (cardMembers.isEmpty()) null else {
        { CardManager(imgPath = "") } // TODO : Card Manager 이미지 path 연결하기
    }

@Composable
fun CardManager(modifier: Modifier = Modifier, imgPath: String) {
    AsyncImage(
        model = imgPath,
        modifier = modifier.size(48.dp),
        contentDescription = null,
        contentScale = ContentScale.FillWidth
    )
}