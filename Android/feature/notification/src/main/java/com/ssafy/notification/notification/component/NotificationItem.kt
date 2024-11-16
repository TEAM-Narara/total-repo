package com.ssafy.notification.notification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.R
import com.ssafy.designsystem.formatTimestamp
import com.ssafy.designsystem.values.DarkGray
import com.ssafy.designsystem.values.IconXLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.designsystem.values.TextSmall
import java.time.ZonedDateTime

@Composable
fun NotificationItem(
    modifier: Modifier = Modifier,
    content: AnnotatedString,
    date: Long,
    onClick: () -> Unit = {},
    profile: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(PaddingDefault),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(IconXLarge)
                .clip(CircleShape)
        ) {
            profile()
        }
        Spacer(modifier = Modifier.width(PaddingDefault))
        Column {
            Text(content, fontSize = TextLarge)
            Text(date.formatTimestamp(), fontSize = TextSmall, color = DarkGray)
        }
    }
}

@Composable
fun NotificationItem(
    modifier: Modifier = Modifier,
    content: String,
    date: Long,
    onClick: () -> Unit = {},
    profile: @Composable () -> Unit
) = NotificationItem(
    modifier = modifier,
    content = buildAnnotatedString { append(content) },
    date = date,
    onClick = onClick,
    profile = profile
)


@Preview
@Composable
private fun NotificationItemPreview() {
    NotificationItem(
        content = "contentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontentcontent",
        date = ZonedDateTime.now().toEpochSecond()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            modifier = Modifier.fillMaxSize(),
            contentDescription = "",
            contentScale = ContentScale.FillWidth
        )
    }
}