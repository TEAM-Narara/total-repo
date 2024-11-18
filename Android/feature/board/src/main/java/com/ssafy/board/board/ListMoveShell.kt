package com.ssafy.board.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ssafy.board.board.components.card.Cover
import com.ssafy.board.board.components.card.Label
import com.ssafy.board.board.components.card.Manager
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ReorderCardData
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.component.IconText
import com.ssafy.designsystem.formatRangeTimeStampMill
import com.ssafy.designsystem.formatTimestampMill
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationDefault
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.IconSmall
import com.ssafy.designsystem.values.ListWidth
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.White

@Composable
fun ListItemShell(
    title: String,
    isWatching: Boolean,
    cards: List<ReorderCardData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(ListWidth),
        shape = RoundedCornerShape(CornerMedium),
        colors = CardDefaults.cardColors().copy(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ElevationDefault
        ),
    ) {
        Column(
            modifier = modifier
                .padding(PaddingDefault)
                .weight(1f, false),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EditableText(
                    text = title,
                    maxTitleLength = 15
                )

                Spacer(modifier = Modifier.weight(1f))

                if (isWatching) {
                    Image(
                        modifier = Modifier.size(IconMedium),
                        painter = painterResource(id = R.drawable.watch),
                        contentDescription = "watch",
                    )
                }
            }

            Column {
                Spacer(modifier = Modifier.height(PaddingDefault))
                cards.forEach { card ->
                    CardItemShell(
                        card = card.cardData
                    )
                }
                Spacer(modifier = Modifier.height(PaddingDefault))
            }
        }
    }
}

@Composable
fun CardItemShell(
    modifier: Modifier = Modifier,
    card: CardData
) {
    Card(
        shape = RoundedCornerShape(CornerMedium),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ElevationDefault
        ),
    ) {
        if (card.Cover != null) Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.7f),
        ) {
            card.Cover?.let { it() }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(PaddingXSmall),
            modifier = Modifier.padding(vertical = PaddingDefault, horizontal = PaddingMedium),
        ) {
            if (card.Label != null) Row(horizontalArrangement = Arrangement.spacedBy(PaddingXSmall)) { card.Label?.let { it() } }
            Row {
                Text(text = card.name, fontSize = TextSmall, modifier = Modifier.weight(1f))
                if (!card.isSynced) Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    modifier = Modifier.size(
                        IconSmall
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (card.startAt != null && card.endAt != null) {
                    IconText(
                        icon = Icons.Default.AccessTime,
                        text = formatRangeTimeStampMill(card.startAt, card.endAt)
                    )
                } else if (card.startAt != null) {
                    IconText(icon = Icons.Default.AccessTime, text = card.startAt.formatTimestampMill())
                } else if (card.endAt != null) {
                    IconText(icon = Icons.Default.AccessTime, text = card.endAt.formatTimestampMill())
                }

                if (card.description != null) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Subject,
                        contentDescription = null,
                        modifier = Modifier.size(IconSmall),
                        tint = Color.Gray
                    )
                }

                if (card.attachment) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(IconSmall),
                        tint = Color.Gray
                    )
                }

                if (card.replyCount != 0) {
                    IconText(
                        icon = Icons.Default.ChatBubbleOutline,
                        text = "${card.replyCount}"
                    )
                }
            }

            if (card.Manager != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PaddingXSmall, Alignment.End),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    card.Manager?.let { it() }
                }
            }
        }
    }
}