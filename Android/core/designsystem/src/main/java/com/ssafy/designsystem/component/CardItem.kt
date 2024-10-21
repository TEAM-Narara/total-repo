package com.ssafy.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.R
import com.ssafy.designsystem.formatRangeTimeStamp
import com.ssafy.designsystem.formatTimestamp
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationDefault
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.White
import java.util.Date

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    title: String,
    image: (@Composable () -> Unit)? = null,
    labels: (@Composable RowScope.() -> Unit)? = null,
    startTime: Long? = null,
    endTime: Long? = null,
    description: Boolean = false,
    commentCount: Int = 0,
    manager: (@Composable RowScope.() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(CornerMedium),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ElevationDefault
        ),
    ) {
        if (image != null) Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(),
        ) {
            image()
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(PaddingXSmall),
            modifier = Modifier.padding(vertical = PaddingDefault, horizontal = PaddingMedium),
        ) {
            if (labels != null) Row(horizontalArrangement = Arrangement.spacedBy(PaddingXSmall)) { labels() }
            Text(text = title, fontSize = TextSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                if (startTime != null && endTime != null) {
                    IconText(
                        icon = Icons.Default.AccessTime,
                        text = formatRangeTimeStamp(startTime, endTime)
                    )
                } else if (startTime != null) {
                    IconText(icon = Icons.Default.AccessTime, text = startTime.formatTimestamp())
                } else if (endTime != null) {
                    IconText(icon = Icons.Default.AccessTime, text = endTime.formatTimestamp())
                }
                if (description) IconText(icon = Icons.AutoMirrored.Filled.Subject, text = "")
                if (commentCount != 0) IconText(
                    icon = Icons.Outlined.ChatBubble,
                    text = "$commentCount"
                )
            }
            if (manager != null) Row(
                horizontalArrangement = Arrangement.spacedBy(PaddingXSmall, Alignment.End),
                modifier = Modifier.fillMaxWidth()
            ) {
                manager()
            }
        }
    }
}

@Preview
@Composable
fun CardPreview() {

    CardItem(
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