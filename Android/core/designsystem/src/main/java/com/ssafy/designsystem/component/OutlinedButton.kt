package com.ssafy.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.ssafy.designsystem.values.BorderDefault
import com.ssafy.designsystem.values.CornerSmall
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium

@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Primary,
    textColor: Color = Primary,
    cornerRadius: Dp = CornerSmall
) {
    OutlinedButton(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(width = BorderDefault, color = color)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = TextMedium
        )
    }
}
