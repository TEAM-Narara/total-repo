package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.formatTimestampSec
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall

@Composable
fun ActivityLog(
    icon: @Composable () -> Unit,
    content: String,
    editDate: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingDefault, PaddingZero),
        horizontalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {

        Box(
            modifier = Modifier
                .size(IconLarge)
                .clip(CircleShape)
        ) {
            icon()
        }

        Column {
            Text(text = content, fontSize = TextMedium)
            Text(text = editDate.formatTimestampSec(), fontSize = TextSmall, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    ActivityLog(content = "손오공", editDate = 20000, icon = {})
}