package com.ssafy.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.ssafy.designsystem.values.LightGray
import com.ssafy.designsystem.values.PaddingOne
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditText(
    title: String,
    modifier: Modifier = Modifier,
    text: String = "",
    onTextChange: (String) -> Unit,
    textHint: String = "",
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.padding(start = PaddingOne)
    ) {

        Text(
            text = title,
            fontSize = TextSmall
        )

        BasicTextField(
            value = text,
            modifier = modifier.fillMaxWidth(),
            onValueChange = onTextChange,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp
            ),
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = text,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    placeholder = {
                        Text(
                            text = textHint,
                            color = LightGray,
                        )
                    },
                    contentPadding = PaddingValues(
                        start = PaddingZero,
                        top = PaddingSmall,
                        end = PaddingZero,
                        bottom = PaddingSmall
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    )
                )
            }
        )
    }
}
