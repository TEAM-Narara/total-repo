package com.ssafy.home.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.BoardItem
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.Yellow
import com.ssafy.model.board.BoardDTO

@Composable
fun HomeBodyScreen(
    modifier: Modifier = Modifier,
    boards: List<BoardDTO>,
    spanCount: Int,
    moveToBoardScreen: (Long) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier
            .padding(horizontal = PaddingDefault)
            .padding(bottom = PaddingDefault),
        columns = GridCells.Fixed(spanCount),
        horizontalArrangement = Arrangement.spacedBy(PaddingDefault),
        verticalArrangement = Arrangement.spacedBy(PaddingDefault)
    ) {
        item(span = { GridItemSpan(spanCount) }) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = PaddingDefault),
                contentScale = ContentScale.Crop
            )
        }

        items(boards.size) {
            val board = boards[it]
            BoardItem(
                title = board.name ?: "보드-${it}",
                onBoardClick = { moveToBoardScreen(it.toLong()) },
                containerColor = board.cover?.value?.toLongOrNull()?.let(::Color) ?: Yellow,
            )
        }
    }
}