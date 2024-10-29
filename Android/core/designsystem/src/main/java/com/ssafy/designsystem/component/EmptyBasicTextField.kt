package com.ssafy.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun EmptyBasicTextField(
    modifier: Modifier = Modifier,
    content: String,
    setContent: (String) -> Unit,
    isFocus: Boolean = true,
) {

    val focusRequester = remember(isFocus) { FocusRequester() }

    LaunchedEffect(isFocus) {
        if (isFocus) {
            focusRequester.requestFocus()
        }
    }

    BasicTransparentTextField(
        value = content,
        onValueChange = setContent,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
    )

}