package com.ssafy.card.card.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ssafy.designsystem.values.MascotDefault
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.RadiusLarge

fun LazyListScope.cardAttachmentInfo(
    modifier: Modifier = Modifier,
    attachments: List<String>,
    addPhoto: () -> Unit
) {
    item {
        Column(modifier = modifier.fillMaxWidth()) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.AttachFile, contentDescription = "첨부파일")
                Text(
                    text = "첨부파일", modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = PaddingSmall)
                )
                IconButton(onClick = addPhoto) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "추가")
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(PaddingDefault)) {
                items(attachments.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(MascotDefault)
                            .clip(RoundedCornerShape(RadiusLarge))
                    ) {
                        AsyncImage(
                            model = attachments[index],
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
