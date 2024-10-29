package com.ssafy.card.card.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.EditableMarkDownText
import com.ssafy.designsystem.formatTimestamp
import com.ssafy.designsystem.formatUnixTimeStamp
import com.ssafy.designsystem.values.IconLegendLarge
import com.ssafy.designsystem.values.LabelHeight
import com.ssafy.designsystem.values.LabelWidth
import com.ssafy.designsystem.values.RadiusDefault
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.model.card.CardDTO

fun LazyListScope.cardInfoScreen(
    modifier: Modifier = Modifier,
    cardDTO: CardDTO,
    onClickLabel: () -> Unit,
    onClickDate: () -> Unit,
    isContentFocus: Boolean = false,
    setContentFocus: (Boolean) -> Unit,
    setContent: (String) -> Unit
) {

    if (cardDTO.attachments.isNotEmpty()) {
        val attachment = cardDTO.attachments.first()
        item(key = attachment) {
            AsyncImage(
                model = attachment,
                contentDescription = null,
                modifier = Modifier.height(IconLegendLarge),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        item(key = "empty") {
            Box(
                modifier = Modifier
                    .height(IconLegendLarge)
                    .fillMaxWidth()
                    .background(color = Color.LightGray)
            )
        }
    }

    item(key = cardDTO.title) {
        Text(
            text = cardDTO.title,
            fontSize = TextLarge,
            fontWeight = FontWeight.Bold,
            modifier = modifier
        )
    }

    item(key = cardDTO.boardTitle + cardDTO.listTitle) {
        Text(
            text = "${cardDTO.boardTitle} / ${cardDTO.listTitle}",
            modifier = modifier
        )
    }

    item(key = cardDTO.labels) {
        ClickableIconText(
            modifier = modifier,
            icon = Icons.Outlined.BookmarkBorder,
            onClickAction = onClickLabel,
            contents = {
                cardDTO.labels.forEach { label ->
                    Box(
                        modifier = Modifier
                            .width(LabelWidth)
                            .height(LabelHeight)
                            .clip(RoundedCornerShape(RadiusDefault))
                            .background(color = Color(label.color))
                    )
                }
            },
        )
    }


    item(key = cardDTO.startDate + cardDTO.endDate) {
        ClickableIconText(
            modifier = modifier,
            icon = Icons.Outlined.Timer,
            onClickAction = onClickDate,
            contents = {
                when {
                    cardDTO.startDate != 0L && cardDTO.endDate != 0L ->
                        Text(text = formatUnixTimeStamp(cardDTO.startDate, cardDTO.endDate))

                    cardDTO.startDate != 0L ->
                        Text(text = cardDTO.startDate.formatTimestamp())

                    cardDTO.endDate != 0L ->
                        Text(text = cardDTO.endDate.formatTimestamp())
                }
            }
        )
    }

    item(key = cardDTO.content + isContentFocus) {
        EditableMarkDownText(
            modifier = modifier,
            icon = Icons.AutoMirrored.Default.Notes,
            content = cardDTO.content,
            setContent = setContent,
            isFocus = isContentFocus,
            setFocus = setContentFocus
        )
    }

}
