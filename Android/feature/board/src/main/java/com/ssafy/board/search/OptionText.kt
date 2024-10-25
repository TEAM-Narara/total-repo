package com.ssafy.board.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.ssafy.board.search.dto.IconType
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.RadiusSMall
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.Transparent

@Composable
fun OptionText(
    modifier: Modifier = Modifier,
    startIcon: IconType = IconType.None,
    content: String,
    backGroundColor: Color = Transparent,
    endIcon: IconType = IconType.None,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RadiusSMall))
            .background(color = backGroundColor)
    ) {
        DrawIcon(iconType = startIcon, size = IconLarge)
        Text(
            text = content,
            fontSize = TextMedium,
            modifier = Modifier.padding(vertical = PaddingXSmall, horizontal = PaddingSmall)
        )
        DrawIcon(iconType = endIcon, size = IconMedium)
    }
}

@Composable
private fun DrawIcon(iconType: IconType, size: Dp) {

    when (iconType) {
        is IconType.Image -> {
            AsyncImage(
                model = iconType.imageUrl,
                contentDescription = "이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        }

        is IconType.Vector -> {
            Image(
                imageVector = iconType.image,
                contentDescription = "이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color = iconType.backgroundColor)
                    .padding(PaddingXSmall),
            )
        }

        IconType.None -> {}
    }

}
