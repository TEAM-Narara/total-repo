package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.ssafy.designsystem.values.CornerSmall
import com.ssafy.designsystem.values.ReversePrimary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@Composable
fun NegativeButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    color: Color = ReversePrimary,
    textColor: Color = White,
    cornerRadius: Dp = CornerSmall
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = TextMedium
        )
    }
}
