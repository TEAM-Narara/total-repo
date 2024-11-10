package com.ssafy.board.board.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationDefault
import com.ssafy.designsystem.values.ListWidth
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@Composable
fun AddListButton(
    modifier: Modifier = Modifier,
    addList: (String) -> Unit,
    onFocus: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocused) {
        if (isFocused) focusRequester.requestFocus()
    }

    Card(
        modifier = modifier.width(ListWidth),
        shape = RoundedCornerShape(CornerMedium),
        colors = CardDefaults.cardColors().copy(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = ElevationDefault),
        onClick = { isFocused = true }
    ) {
        if (!isFocused) {
            Text(
                text = "+ Add List",
                modifier = Modifier.padding(PaddingDefault),
                fontSize = TextMedium,
            )
        } else {
            EditableText(
                modifier = Modifier
                    .padding(PaddingDefault)
                    .onFocusChanged { if (it.isFocused) onFocus() }
                    .focusRequester(focusRequester),
                onTextChanged = { onFocus() },
                onInputFinished = {
                    isFocused = false
                    addList(it)
                }
            )
        }
    }
}

@Preview
@Composable
private fun AddListButtonPreview() {
    AddListButton(addList = {})
}