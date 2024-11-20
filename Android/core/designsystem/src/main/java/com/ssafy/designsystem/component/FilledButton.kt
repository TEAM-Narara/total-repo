package com.ssafy.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.values.CornerSmall
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@Composable
fun FilledButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    color: Color = Primary,
    textColor: Color = White,
    cornerRadius: Dp = CornerSmall,
    borderSize: Dp = 0.dp,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(borderSize, Primary),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        enabled = enabled
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = TextMedium
        )
    }
}
