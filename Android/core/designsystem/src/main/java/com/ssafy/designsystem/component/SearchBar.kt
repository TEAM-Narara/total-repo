package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.values.PaddingSmall

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    text: String = "",
    onTextChanged: (String) -> Unit = {},
    prefix: @Composable () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            prefix()

            EditableText(
                text = text,
                onTextChanged = onTextChanged,
                modifier = Modifier
                    .padding(start = PaddingSmall)
                    .weight(1f),
                maxTitleLength = 40,
            )

            IconButton(
                imageVector = Icons.Default.Search,
                contentDescription = "검색",
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray
        )
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    SearchBar {
        IconButton(imageVector = Icons.Default.ArrowBack, contentDescription = "")
    }
}