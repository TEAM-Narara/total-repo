package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.ssafy.designsystem.values.Black
import com.ssafy.designsystem.values.DarkGray
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
    textColor: Color = Black,
    alignStyle: TextAlign = TextAlign.Start,
    placeholder: String? = null
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
        textStyle = TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = alignStyle,
            color = textColor
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onInputFinished(value)
            },
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        color = DarkGray,
                        fontSize = fontSize,
                        fontWeight = fontWeight,
                        textAlign = alignStyle
                    )
                }
                innerTextField()
            }
        }
    )
}