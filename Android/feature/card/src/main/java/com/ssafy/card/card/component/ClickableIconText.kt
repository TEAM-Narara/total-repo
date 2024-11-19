package com.ssafy.card.card.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.values.PaddingSmall

@Composable
fun ClickableIconText(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contents: @Composable RowScope.() -> Unit,
    onClickAction: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClickAction
            )
    ) {
        Icon(imageVector = icon, contentDescription = "icon")
        contents()
    }
}

@Composable
@Preview
fun ClickableIconTextPreview() {
    ClickableIconText(
        icon = Icons.Default.Add,
        contents = {
            Text(text = "Add")
        },
        onClickAction = {}
    )
}

