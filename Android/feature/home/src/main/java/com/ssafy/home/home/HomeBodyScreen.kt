package com.ssafy.home.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.BoardItem
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.Yellow
import com.ssafy.designsystem.values.toColor
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.with.CoverType

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
                title = board.name,
                onBoardClick = { moveToBoardScreen(board.id) },
                background = {
                    when (board.cover.type) {
                        CoverType.NONE -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = Yellow)
                            )
                        }

                        CoverType.COLOR -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = board.cover.value.toColor())
                            )
                        }

                        CoverType.IMAGE -> {
                            AsyncImage(
                                model = board.cover.value,
                                contentDescription = "Board Cover",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            )
        }
    }
}