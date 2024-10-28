package com.ssafy.board.board

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun handleLazyListScroll(
    lazyListState: LazyListState,
    dropIndex: Int,
): Unit = coroutineScope {
    val visibleItemsInfo = lazyListState.layoutInfo.visibleItemsInfo

    val itemInfo = visibleItemsInfo.firstOrNull()
    val itemSize = itemInfo?.size ?: 0
    val centerOffset = itemSize / 2

    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
    val lastVisibleItemIndex = firstVisibleItemIndex + visibleItemsInfo.size - 1

    val scrollOffset = when (dropIndex) {
        0 -> -itemSize
        firstVisibleItemIndex -> -centerOffset
        lastVisibleItemIndex -> centerOffset
        else -> return@coroutineScope
    }

    val targetIndex = when (dropIndex) {
        firstVisibleItemIndex -> firstVisibleItemIndex
        lastVisibleItemIndex -> firstVisibleItemIndex + 1
        else -> return@coroutineScope
    }

    lazyListState.smoothScrollToItem(
        index = targetIndex,
        scrollOffset = scrollOffset,
        duration = 200
    )
}

suspend fun handleLazyListScrollToCenter(
    lazyListState: LazyListState,
    dropIndex: Int,
): Unit = coroutineScope {
    val viewportSize = lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset

    val itemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()
    val itemSize = itemInfo?.size ?: 0

    val centerOffset = (viewportSize - itemSize) / 2

    launch {
        lazyListState.smoothScrollToItem(
            index = dropIndex,
            scrollOffset = -centerOffset,
        )
    }
}


suspend fun LazyListState.smoothScrollToItem(
    index: Int,
    scrollOffset: Int = 0,
    duration: Int = 500,
    easing: Easing = LinearEasing
) {
    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }

    if (itemInfo != null) {
        animateScrollBy(
            value = (itemInfo.offset + scrollOffset).toFloat(),
            animationSpec = tween(
                durationMillis = duration,
                easing = easing
            )
        )
    } else {
        animateScrollToItem(
            index = index,
            scrollOffset = scrollOffset
        )
    }
}
