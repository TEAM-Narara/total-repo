package com.ssafy.card.card.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ssafy.designsystem.values.IconXLarge
import com.ssafy.designsystem.values.PaddingSmall

fun LazyListScope.cardMemberInfo(
    modifier: Modifier = Modifier,
    members: List<String>,
    showCardMembers: () -> Unit
) {
    item {
        Column(modifier = modifier.fillMaxWidth()) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Accessibility, contentDescription = "담당자")
                Text(
                    text = "담당자", modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = PaddingSmall)
                )
                IconButton(onClick = showCardMembers) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "추가")
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                items(members.size) { index ->
                    Box(
                        modifier = Modifier
                            .height(IconXLarge)
                            .clip(CircleShape)
                            .aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = members[index],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
