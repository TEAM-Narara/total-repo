package com.ssafy.designsystem.component

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.ssafy.designsystem.values.TextMedium

@Composable
fun EditableText(
    modifier: Modifier = Modifier,
    text: String = "",
    onTextChanged: (String) -> Unit = {},
    onInputFinished: (String) -> Unit = {},
    maxTitleLength: Int = 15,
    alignStyle: TextAlign = TextAlign.Start
) {
    val (value, onValueChange) = remember { mutableStateOf(text) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= maxTitleLength) {
                onValueChange(newValue)
                onTextChanged(newValue)
            }
        },
        modifier = modifier,
        singleLine = true,
        textStyle = TextStyle(fontSize = TextMedium, textAlign = alignStyle),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onInputFinished(value)
            }
        )
    )
}