package com.ssafy.home.mycard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.CardItem
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingSemiLarge
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.Transparent
import com.ssafy.designsystem.values.White
import com.ssafy.model.with.BoardInMyRepresentativeCard

@Composable
fun BoardWithMyCards(
    modifier: Modifier = Modifier,
    board: BoardInMyRepresentativeCard,
    boardIcon: @Composable () -> Unit,
    onClick: (cardId: Long) -> Unit
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

                Text(
                    text = board.name,
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

            items(board.cards.size) { index ->
                val card = board.cards[index]
                CardItem(
                    modifier = modifier,
                    title = card.name,
                    image = { /*이미지 할당 */ },
                    labels = { /*라벨 할당 */ },
                    startTime = card.startAt,
                    endTime = card.endAt,
                    description = card.description != null,
                    attachment = card.isAttachment,
                    commentCount = card.replyCount,
                    manager = {
                        card.cardMembers.forEach { member ->
                            AsyncImage(
                                model = member.memberProfileImgUrl,
                                contentDescription = "Member Profile Image",
                                contentScale = ContentScale.Crop,
                                error = rememberVectorPainter(Icons.Default.AccountCircle),
                            )
                        }
                    },
                    onClick = { onClick(card.id) }
                )
            }
        }
    }
}
