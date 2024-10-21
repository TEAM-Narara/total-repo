package com.ssafy.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.White

@Composable
fun TitledBox(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    contents: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(color = White)
            .padding(horizontal = PaddingDefault, vertical = PaddingMedium)
    ) {
        title()
        Spacer(modifier = Modifier.height(PaddingMedium))
        contents()
    }
}

@Preview
@Composable
fun TitledBoxPreview() {
    TitledBox(title = { IconTitlePreview() }) {
        Text(text = "???????")
    }
}