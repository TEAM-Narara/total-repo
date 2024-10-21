package com.ssafy.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.PaddingTwo
import com.ssafy.designsystem.values.TextMedium

@Composable
fun LoginButton(
    onClick: () -> Unit,
    icon: Painter? = null,
    backColor: Color,
    textColor: Color,
    content: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp), // 둥근 모서리 설정
        colors = buttonColors(backColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Image(
                    painter = it,
                    contentDescription = content,
                    modifier = Modifier
                        .size(IconMedium)
                        .weight(1.0f)
                )
            }
            Spacer(modifier = Modifier.weight(weight = .5f))
            Text(
                text = content,
                fontSize = TextMedium,
                color = textColor,
                modifier = Modifier
                    .padding(PaddingTwo)
            )
            Spacer(modifier = Modifier.weight(weight = 1f))
        }
    }
}
