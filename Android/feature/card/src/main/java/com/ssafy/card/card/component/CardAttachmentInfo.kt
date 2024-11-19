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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ScreenshotMonitor
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.IconButton
import com.ssafy.designsystem.component.IconText
import com.ssafy.designsystem.values.MascotDefault
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.RadiusLarge
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White
import com.ssafy.model.with.AttachmentDTO

fun LazyListScope.cardAttachmentInfo(
    modifier: Modifier = Modifier,
    attachments: List<AttachmentDTO>,
    addPhoto: () -> Unit,
    deleteAttachment: (Long) -> Unit,
    updateAttachmentToCover: (Long) -> Unit,
) {
    item {
        Column(modifier = modifier.fillMaxWidth()) {
            var selectedAttachment by remember { mutableStateOf<AttachmentDTO?>(null) }

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
                IconButton(imageVector = Icons.Default.Add, onClick = addPhoto)
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(PaddingDefault)) {
                items(attachments) { attachment ->
                    Box(
                        modifier = Modifier
                            .size(MascotDefault)
                            .clip(RoundedCornerShape(RadiusLarge))
                    ) {
                        AsyncImage(
                            model = attachment.url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )

                        IconButton(
                            modifier = Modifier.align(Alignment.TopEnd),
                            imageVector = Icons.Default.MoreVert,
                            onClick = { selectedAttachment = attachment }
                        )

                        DropdownMenu(
                            expanded = selectedAttachment?.id == attachment.id,
                            onDismissRequest = { selectedAttachment = null },
                            containerColor = White
                        ) {
                            DropdownMenuItem(
                                text = {
                                    IconText(
                                        icon = Icons.Default.ScreenshotMonitor,
                                        text = "커버로 설정",
                                        fontSize = TextMedium,
                                        fontWeight = FontWeight.Normal,
                                        space = PaddingXSmall
                                    )
                                }, onClick = {
                                    selectedAttachment?.let { updateAttachmentToCover(it.id) }
                                    selectedAttachment = null
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    IconText(
                                        icon = Icons.Default.Delete,
                                        text = "삭제",
                                        fontSize = TextMedium,
                                        fontWeight = FontWeight.Normal,
                                        space = PaddingXSmall
                                    )
                                }, onClick = {
                                    selectedAttachment?.let { deleteAttachment(it.id) }
                                    selectedAttachment = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
