package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.values.IconSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.TextXSmall

@Composable
fun IconText(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    iconDescription: String = "icon"
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PaddingXSmall)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
            modifier = modifier.size(IconSmall)
        )
        Text(text = text, fontSize = TextXSmall)
    }
}

@Preview
@Composable
fun IconTextPreview() {
    IconText(icon = Icons.Outlined.Face, text = "text")
}