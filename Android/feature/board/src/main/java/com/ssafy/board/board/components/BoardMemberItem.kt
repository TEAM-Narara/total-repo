package com.ssafy.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.IconXLarge
import com.ssafy.designsystem.values.PEOPLE_ICON
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.TextXLarge

@Composable
fun BoardMemberItem(
    modifier: Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PaddingDefault),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Groups,
            contentDescription = PEOPLE_ICON,
            modifier = Modifier.size(IconLarge)
        )
        Column(verticalArrangement = Arrangement.spacedBy(PaddingDefault)) {
            Text(text = "Members", fontSize = TextXLarge)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
            ) {
                val images =
                    List(3) { "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z" }
                items(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .height(IconXLarge)
                            .clip(CircleShape)
                            .aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = images[index],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            FilledButton(text = "Invite", onClick = { /*TODO*/ })
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    BoardMemberItem(Modifier)
}
