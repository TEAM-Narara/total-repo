package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.TextMedium
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun EditableMarkDownText(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: String,
    setContent: (String) -> Unit,
    isFocus: Boolean = false,
    setFocus: (Boolean) -> Unit,
) {
    val (text, setText) = remember { mutableStateOf(content) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(PaddingDefault),
        modifier = modifier.fillMaxWidth()
    ) {

        if (icon != null) {
            Icon(imageVector = icon, contentDescription = "아이콘")
        }

        if (!isFocus) {
            MarkdownText(
                fontSize = TextMedium,
                markdown = text,
                onClick = { setFocus(true) },
            )
        } else {
            EmptyBasicTextField(
                content = text,
                setContent = {
                    setText(it)
                    setContent(it)
                },
            )
        }
    }
}
