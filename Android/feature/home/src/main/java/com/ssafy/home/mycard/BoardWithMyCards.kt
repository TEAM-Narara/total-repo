package com.ssafy.home.mycard

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.CardItem
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.LabelHeight
import com.ssafy.designsystem.values.LabelWidth
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingSemiLarge
import com.ssafy.designsystem.values.RadiusDefault
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.Transparent
import com.ssafy.designsystem.values.White
import com.ssafy.designsystem.values.toColor
import com.ssafy.model.with.BoardInMyRepresentativeCard
import com.ssafy.model.with.CoverType

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
                val type = runCatching { CoverType.valueOf(card.coverType ?: "") }
                    .getOrDefault(CoverType.NONE)

                CardItem(
                    modifier = modifier,
                    title = card.name,
                    image = {
                        card.coverValue?.let {
                            if (type == CoverType.IMAGE) CardCoverImage(imgPath = it)
                            else CardCoverColor(color = it.toColor())
                        }
                    },
                    labels = {
                        card.cardLabels.forEach { label ->
                            CardLabel(
                                modifier = Modifier.size(LabelWidth, LabelHeight),
                                color = Color(label.labelColor)
                            )
                        }
                    },
                    startTime = card.startAt,
                    endTime = card.endAt,
                    description = card.description != null,
                    attachment = card.isAttachment,
                    commentCount = card.replyCount,
                    manager = {
                        card.cardMembers.forEach { member ->
                            AsyncImage(
                                model = member.memberProfileImgUrl,
                                modifier = modifier.size(48.dp),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                error = rememberVectorPainter(Icons.Default.AccountCircle)
                            )
                        }
                    },
                    onClick = { onClick(card.id) }
                )
            }
        }
    }
}

@Composable
fun CardCoverColor(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color)
    )
}

@Composable
fun CardCoverImage(modifier: Modifier = Modifier, imgPath: String) {
    AsyncImage(
        model = imgPath,
        modifier = modifier.fillMaxSize(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
}

@Composable
fun CardLabel(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier = modifier
            .width(LabelWidth)
            .height(LabelHeight)
            .clip(RoundedCornerShape(RadiusDefault))
            .background(color = color)
    )
}