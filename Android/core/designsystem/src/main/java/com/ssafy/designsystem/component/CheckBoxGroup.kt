package com.ssafy.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.TextMedium

@Composable
fun <T> CheckBoxGroup(
    modifier: Modifier = Modifier,
    title: String,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    isOptionChecked: (T) -> Boolean,
    option: @Composable (T) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingSmall)
    ) {

        Text(text = title, fontSize = TextMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(PaddingSmall))

        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                onOptionSelected(option)
            }) {
                Checkbox(
                    checked = isOptionChecked(option),
                    onCheckedChange = { onOptionSelected(option) }
                )

                Box(
                    modifier = Modifier
                        .padding(start = PaddingSmall)
                        .weight(1f)
                ) {
                    option(option)
                }
            }
        }
    }
}
