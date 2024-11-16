package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.values.IconSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.TextXSmall

@Composable
fun IconText(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    iconDescription: String = "icon",
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextXSmall,
    space: Dp = 0.dp,
    tint: Color = LocalContentColor.current,
    color: Color = Color.Unspecified,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PaddingXSmall)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
            modifier = Modifier.size(IconSmall),
            tint = tint
        )
        Spacer(modifier = Modifier.width(space))
        Text(text = text, fontSize = fontSize, fontWeight = fontWeight, color = color)
    }
}

@Preview
@Composable
fun IconTextPreview() {
    IconText(icon = Icons.Outlined.Face, text = "text")
}