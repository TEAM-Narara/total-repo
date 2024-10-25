package com.ssafy.home.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.BoardItem
import com.ssafy.designsystem.component.CardItem
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.component.IconButton
import com.ssafy.designsystem.values.BoardWidth
import com.ssafy.designsystem.values.CardWidth
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingSemiLarge
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.Transparent
import com.ssafy.designsystem.values.Yellow
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    boardList: List<Any>,
    onBackPressed: () -> Unit,
    moveToCardScreen: (Any) -> Unit
) {
    val (keyword, onValueChanged) = remember { mutableStateOf("Search") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingSmall)
            .verticalScroll(scrollState),
    ) {
        Row(
            modifier = Modifier.padding(PaddingZero, PaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(PaddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로 아이콘",
                onClick = onBackPressed
            )
            EditableText(
                text = keyword,
                onTextChanged = {
                    onValueChanged(it)
                    // TODO: data 받아와야함.
                },
                modifier = Modifier.weight(1f),
                maxTitleLength = 40,
            )
            IconButton(
                imageVector = Icons.Default.Search,
                contentDescription = "검색 아이콘",
                onClick = {
                    //TODO: 검색 처리
                })
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingZero),
            thickness = 1.dp,
            color = Color.Gray
        )
        Text(text = "Board", modifier = Modifier.padding(PaddingSmall))
        LazyRow(
            modifier = Modifier
                .background(color = Transparent),
            horizontalArrangement = Arrangement.spacedBy(PaddingDefault),
            contentPadding = PaddingValues(start = PaddingSmall)
        ) {
            items(5) {
//                items(boards.size) {TODO : Board에 대한 정보를 전달합니다.
                BoardItem(
                    title = "Board $it",
                    onBoardClick = { },
                    containerColor = Yellow,
                    onMenuClick = { /*TODO*/ },
                    modifier = Modifier.width(BoardWidth)
                )
            }
        }
        Text(text = "Card", modifier = Modifier.padding(PaddingSmall))
        LazyRow(
            modifier = Modifier
                .background(color = Transparent),
            horizontalArrangement = Arrangement.spacedBy(PaddingSemiLarge),
            contentPadding = PaddingValues(start = PaddingSmall)
        ) {
            // TODO : Board 내부의 Card 리스트의 개수에 따라 수정
            items(5) { item ->
                CardItem(
                    onClick = {
                        moveToCardScreen(0)
                    },
                    title = "제목", startTime = Date().time, commentCount = 1,
                    image = {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth
                        )
                    },
                    modifier = Modifier.width(CardWidth),
                    description = true,
                    manager = {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            modifier = Modifier.size(48.dp),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth
                        )
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            modifier = Modifier.size(48.dp),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth
                        )
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            modifier = Modifier.size(48.dp),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    SearchScreen(
        List(10) { Any() },
        {},
        {}
    )
}