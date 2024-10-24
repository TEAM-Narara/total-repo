package com.ssafy.board.board

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun handleLazyListScroll(
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
            scrollOffset = centerOffset,
        )
    }
}


suspend fun LazyListState.smoothScrollToItem(
    index: Int,
    scrollOffset: Int = 0,
    duration: Int = 500,
    easing: Easing = FastOutSlowInEasing
) {
    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }

    if (itemInfo != null) {
        animateScrollBy(
            value = (itemInfo.offset - scrollOffset).toFloat(),
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
