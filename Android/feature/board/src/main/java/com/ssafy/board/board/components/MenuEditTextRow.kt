package com.ssafy.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium

@Composable
fun MenuEditTextRow(
    modifier: Modifier,
    title: String,
    content: String,
    changeContent: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PaddingXSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(PaddingDefault, PaddingZero)
    ) {
        Text(text = title, fontSize = TextMedium, color = Primary)
        EditableText(
            text = content,
            onInputFinished = { newName: String -> changeContent(newName) },
            modifier = modifier.weight(1f),
            alignStyle = TextAlign.End
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview2() {
    MenuEditTextRow(Modifier, "name", "boardName") {}
}
