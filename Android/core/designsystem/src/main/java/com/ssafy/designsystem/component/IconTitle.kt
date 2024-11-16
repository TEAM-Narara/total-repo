package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.values.IconSmall
import com.ssafy.designsystem.values.PaddingXSmall

@Composable
fun IconTitle(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector,
    title: String,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconText(icon = leadingIcon, text = title, iconDescription = "leading icon")
        Spacer(modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(PaddingXSmall), content = actions)
    }
}

@Preview
@Composable
fun IconTitlePreview() {
    IconTitle(leadingIcon = Icons.Outlined.Face, title = "text") {
        Icon(
            imageVector = Icons.Outlined.Face,
            contentDescription = "",
            modifier = Modifier.size(IconSmall)
        )
        Icon(
            imageVector = Icons.Outlined.Face,
            contentDescription = "",
            modifier = Modifier.size(IconSmall)
        )
        Icon(
            imageVector = Icons.Outlined.Face,
            contentDescription = "",
            modifier = Modifier.size(IconSmall)
        )
        Icon(
            imageVector = Icons.Outlined.Face,
            contentDescription = "",
            modifier = Modifier.size(IconSmall)
        )
    }
}