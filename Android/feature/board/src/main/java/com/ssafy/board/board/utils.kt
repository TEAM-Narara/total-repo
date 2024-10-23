package com.ssafy.board.board

import androidx.compose.foundation.lazy.LazyListState
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
