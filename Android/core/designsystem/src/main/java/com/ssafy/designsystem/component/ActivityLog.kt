package com.ssafy.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.R
import com.ssafy.designsystem.formatTimestamp
import com.ssafy.designsystem.values.ACTIVITY_ICON
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall

@Composable
fun ActivityLog(
    iconResId: Int,
    content: String,
    editDate: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingZero, PaddingXSmall)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = ACTIVITY_ICON,
            modifier = modifier
                .size(IconLarge)
                .clip(CircleShape)
                .padding(PaddingZero, PaddingXSmall, PaddingSmall, PaddingZero)
        )
        Column {
            Text(text = content, fontSize = TextMedium, fontWeight = FontWeight.SemiBold)
            Text(text = editDate.formatTimestamp(), fontSize = TextSmall)
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    ActivityLog(content = "손오공", editDate = 20000, iconResId = R.drawable.outline_add_reaction_24)
}