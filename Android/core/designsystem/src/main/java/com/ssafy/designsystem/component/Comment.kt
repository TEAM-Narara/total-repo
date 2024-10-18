package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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

@Composable
fun Comment(
    icon: @Composable () -> Unit,
    nickname: String,
    date: Long,
    content: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
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
            Text(text = content, fontSize = TextMedium)
        }

        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "더보기 메뉴"
            )
        }
    }
}
