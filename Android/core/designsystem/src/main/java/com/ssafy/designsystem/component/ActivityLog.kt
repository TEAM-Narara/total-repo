package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddReaction
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.formatTimestamp
import com.ssafy.designsystem.values.ACTIVITY_ICON
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall

@Composable
fun ActivityLog(
    icon: ImageVector,
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
        Icon(
            imageVector = icon,
            contentDescription = ACTIVITY_ICON,
            modifier = modifier.size(IconLarge)
        )
        Column {
            Text(text = content, fontSize = TextMedium)
            Text(text = editDate.formatTimestamp(), fontSize = TextSmall, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    ActivityLog(content = "손오공", editDate = 20000, icon = Icons.Outlined.AddReaction)
}