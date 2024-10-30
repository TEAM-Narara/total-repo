package com.ssafy.board

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun getIcon(type: String): ImageVector {
    return when (type) {
        "rename" -> Icons.Outlined.Keyboard
        "attached" -> Icons.Default.AttachFile
        else -> Icons.Default.AddReaction
    }
}
