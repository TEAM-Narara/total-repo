package com.ssafy.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.values.BoxDefault
import com.ssafy.designsystem.values.Gray
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.TextMedium

@Composable
fun SheetItem(
    modifier: Modifier = Modifier,
    hasDivider: Boolean = false,
    sheetIcon: ImageVector,
    sheetName: String,
    onSheetItemClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(BoxDefault)
            .clickable { onSheetItemClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PaddingDefault)
        ) {
            Icon(imageVector = sheetIcon, contentDescription = "워크 스페이스")
            Spacer(modifier = Modifier.fillMaxWidth(0.1f))
            Text(text = sheetName, fontSize = TextMedium)
        }

        if (hasDivider) {
            HorizontalDivider(thickness = 1.dp, color = Gray)
        }
    }
}