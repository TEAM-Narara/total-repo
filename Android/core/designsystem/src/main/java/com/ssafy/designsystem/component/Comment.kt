package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.ssafy.designsystem.formatTimestamp
import com.ssafy.designsystem.values.DarkGray
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun Comment(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    nickname: String,
    date: Long,
    content: String,
    setContent: (String) -> Unit,
    hasAuth: Boolean = false,
    isFocus: Boolean = false,
    setFocus: (Boolean) -> Unit,
    deleteComment: () -> Unit
) {

    Row(modifier = modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .size(IconLarge)
                .clip(CircleShape)
        ) {
            icon()
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = PaddingMedium)
        ) {
            Text(text = nickname, fontSize = TextMedium)
            Text(text = date.formatTimestamp(), fontSize = TextSmall, color = DarkGray)
            if (hasAuth) {
                EditableMarkDownText(
                    content = content,
                    setContent = setContent,
                    isFocus = isFocus,
                    setFocus = setFocus
                )
            } else {
                MarkdownText(
                    fontSize = TextMedium,
                    markdown = content,
                )
            }
        }

        if (hasAuth) {
            IconButton(onClick = { deleteComment() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "삭제하기"
                )
            }
        }
    }
}
