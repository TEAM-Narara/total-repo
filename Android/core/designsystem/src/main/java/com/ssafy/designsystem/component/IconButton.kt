package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.IconMedium

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    onClick: () -> Unit = {},
    contentDescription: String = "",
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.then(Modifier.size(IconLarge))
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(IconMedium)
        )
    }
}