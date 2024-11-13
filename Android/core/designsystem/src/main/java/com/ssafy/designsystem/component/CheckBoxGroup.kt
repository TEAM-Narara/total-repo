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
    noOptionItem: @Composable () -> Unit = {},
    option: @Composable (T) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingSmall)
    ) {
        Text(text = title, fontSize = TextMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(PaddingSmall))

        noOptionItem()

        options.forEach { option ->
            CheckBoxItem(
                checked = isOptionChecked(option),
                onCheckedChanged = { onOptionSelected(option) }
            ) {
                option(option)
            }
        }
    }
}

@Composable
fun CheckBoxItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChanged: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.clickable {
        onCheckedChanged()
    }) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChanged() }
        )

        Box(
            modifier = Modifier
                .padding(start = PaddingSmall)
                .weight(1f)
        ) {
            content()
        }
    }
}