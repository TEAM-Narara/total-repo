package com.ssafy.board.board.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.ssafy.board.board.data.CardData
import com.ssafy.designsystem.values.LabelHeight
import com.ssafy.designsystem.values.LabelWidth
import com.ssafy.designsystem.values.RadiusDefault

val CardData.Label: (@Composable RowScope.() -> Unit)?
    get() = if (cardLabels.isEmpty()) null else {
        { cardLabels.map { CardLabel(color = Color(it.labelColor)) } }
    }

@Composable
fun CardLabel(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier = modifier
            .width(LabelWidth)
            .height(LabelHeight)
            .clip(RoundedCornerShape(RadiusDefault))
            .background(color = color)
    )
}