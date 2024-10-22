package com.ssafy.home.mycard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.CardItem
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingSemiLarge
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.Transparent
import com.ssafy.designsystem.values.White
import java.util.Date

@Composable
fun BoardWithMyCards(
    modifier: Modifier = Modifier,
    board: Any,
    boardIcon: @Composable () -> Unit,
    onClick: (Any) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Gray)
    ) {

        Surface(
            color = White,
            shadowElevation = 10.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = PaddingMedium,
                        horizontal = PaddingSemiLarge
                    )
            ) {
                Box(
                    modifier = Modifier
                        .width(61.dp)
                        .height(36.dp)
                        .clip(shape = RoundedCornerShape(CornerMedium))
                ) {
                    boardIcon()
                }

                // TODO : Board Name을 표시하는 부분
                Text(
                    text = "Board Name",
                    modifier = Modifier.padding(start = PaddingMedium),
                    fontSize = TextSmall
                )
            }
        }

        LazyRow(
            modifier = Modifier
                .background(color = Transparent)
                .padding(vertical = PaddingSemiLarge),
            horizontalArrangement = Arrangement.spacedBy(PaddingSemiLarge),
            contentPadding = PaddingValues(horizontal = PaddingSemiLarge)
        ) {

            // TODO : Board 내부의 Card 리스트의 개수에 따라 수정
            items(5) { item ->
                CardItem(
                    onClick = { onClick(item) },
                    title = "제목", startTime = Date().time, commentCount = 1,
                    image = {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth
                        )
                    },
                    modifier = Modifier.width(300.dp),
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
