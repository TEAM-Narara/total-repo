package com.ssafy.board.board

import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun handleLazyListScroll(
    lazyListState: LazyListState,
    dropIndex: Int,
    indexOffset: Int = 0,
): Unit = coroutineScope {
    val targetIndex = dropIndex + indexOffset

    val viewportSize = lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset

    val itemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }
    val itemSize = itemInfo?.size ?: 0

    val centerOffset = (itemSize - viewportSize) / 2

    launch {
        lazyListState.animateScrollToItem(
            index = targetIndex,
            scrollOffset = centerOffset,
        )
    }
}
