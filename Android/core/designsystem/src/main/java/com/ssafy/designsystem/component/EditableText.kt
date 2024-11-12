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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.ssafy.designsystem.values.TextMedium

@Composable
fun EditableText(
    modifier: Modifier = Modifier,
    text: String = "",
    onTextChanged: (String) -> Unit = {},
    onInputFinished: (String) -> Unit = {},
    maxTitleLength: Int = 15,
    fontSize: TextUnit = TextMedium,
    fontWeight: FontWeight = FontWeight.Normal,
    alignStyle: TextAlign = TextAlign.Start
) {
    val (value, onValueChange) = remember(text) { mutableStateOf(text) }
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
        textStyle = TextStyle(fontSize = fontSize, fontWeight = fontWeight, textAlign = alignStyle),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onInputFinished(value)
            }
        )
    )
}