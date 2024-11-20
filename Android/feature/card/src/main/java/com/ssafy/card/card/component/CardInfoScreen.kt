package com.ssafy.card.card.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.designsystem.component.EditableMarkDownText
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.formatRangeTimeStampMill
import com.ssafy.designsystem.formatTimestampMill
import com.ssafy.designsystem.values.LabelHeight
import com.ssafy.designsystem.values.LabelWidth
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.RadiusDefault
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.model.card.CardDTO

fun LazyListScope.cardInfoScreen(
    modifier: Modifier = Modifier,
    cardDTO: CardDTO,
    onClickLabel: () -> Unit,
    onClickDate: () -> Unit,
    isTitleFocus: Boolean = false,
    setTitleFocus: (Boolean) -> Unit,
    setTitle: (String) -> Unit,
    isContentFocus: Boolean = false,
    setContentFocus: (Boolean) -> Unit,
    setContent: (String) -> Unit,
) {

    item(key = cardDTO.title + isTitleFocus) {
        val interactionSource = remember { MutableInteractionSource() }

        if (isTitleFocus) {
            EditableText(
                text = cardDTO.title,
                fontSize = TextLarge,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(top = PaddingMedium),
                onTextChanged = setTitle,
                onInputFinished = { setTitleFocus(false) }
            )
        } else {
            Text(
                text = cardDTO.title,
                fontSize = TextLarge,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .padding(top = PaddingMedium)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        setTitleFocus(true)
                    },
            )
        }
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


    item(cardDTO.startDate, cardDTO.endDate) {
        ClickableIconText(
            modifier = modifier,
            icon = Icons.Outlined.Timer,
            onClickAction = onClickDate,
            contents = {
                val startDate = cardDTO.startDate
                val endDate = cardDTO.endDate
                when {
                    startDate != null && endDate != null ->
                        Text(text = formatRangeTimeStampMill(startDate, endDate))

                    startDate != null ->
                        Text(text = startDate.formatTimestampMill())

                    endDate != null ->
                        Text(text = endDate.formatTimestampMill())
                }
            }
        )
    }

    item(key = cardDTO.content) {
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
