package com.ssafy.board

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.coroutineScope

suspend fun handleLazyListScroll(
    lazyListState: LazyListState,
    dropIndex: Int,
    indexOffset: Int,
    isRow: Boolean = false,
): Unit = coroutineScope {
    val targetIndex = dropIndex + indexOffset

    val viewportSize = if (isRow) {
        lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
    } else {
        lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
    }

    val itemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }
    val itemSize = itemInfo?.size ?: 0

    val centerOffset = (viewportSize - itemSize) / 2

    lazyListState.animateScrollToItem(
        index = targetIndex,
        scrollOffset = -centerOffset,
    )
}


@Composable
fun getIcon(type: String): ImageVector {
    return when (type) {
        "rename" -> Icons.Outlined.Keyboard
        "attached" -> Icons.Default.AttachFile
        else -> Icons.Default.AddReaction
    }
}
