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

    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
    val lastVisibleItemIndex = firstVisibleItemIndex + visibleItemsInfo.size - 1

    val scrollOffset = itemSize / 2
    val duration = 200

    when (dropIndex) {
        0 -> lazyListState.smoothScrollToItem(
            index = 0,
            scrollOffset = -itemSize,
            duration = duration
        )

        firstVisibleItemIndex -> lazyListState.smoothScrollToItem(
            index = firstVisibleItemIndex,
            scrollOffset = -scrollOffset,
            duration = duration
        )

        lastVisibleItemIndex -> lazyListState.smoothScrollToItem(
            index = lastVisibleItemIndex,
            scrollOffset = scrollOffset,
            isToLast = true,
            duration = duration
        )
    }
}

suspend fun handleLazyListScrollToCenter(
    lazyListState: LazyListState,
    dropIndex: Int,
): Unit = coroutineScope {
    val viewportSize =
        lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset

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
    easing: Easing = LinearEasing,
    isToLast: Boolean = false
) {
    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }

    if (itemInfo != null) {
        val screenHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val targetOffset = if (isToLast) -screenHeight + itemInfo.size else 0

        animateScrollBy(
            value = (itemInfo.offset + targetOffset + scrollOffset).toFloat(),
            animationSpec = tween(
                durationMillis = duration,
                easing = easing
            )
        )
    } else {
        animateScrollToItem(
            index = index,
            scrollOffset = if (isToLast) {
                -(layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset)
            } else {
                scrollOffset
            }
        )
    }
}