package com.ssafy.designsystem.component

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
import coil3.compose.AsyncImage
import com.ssafy.designsystem.formatTimestamp
import com.ssafy.designsystem.values.DarkGray
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall

// TODO 만약 코멘트에 대한 user 객체가 있다면, User 객체를 파라미터로 전달해야함!!
@Composable
fun Comment(
    iconUrl: String,
    nickname: String,
    date: Long,
    content: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(modifier = modifier.fillMaxWidth()) {
        // TODO 유저 객체의 icon이 앱 DB에 있다면 앱에서 가져오고, 없다면 S3 서버에서 가져와야함!!
        AsyncImage(
            model = iconUrl,
            contentDescription = "유저 이미지",
            modifier = modifier
                .size(IconLarge)
                .clip(CircleShape)
        )

        Column(
            modifier = modifier
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
