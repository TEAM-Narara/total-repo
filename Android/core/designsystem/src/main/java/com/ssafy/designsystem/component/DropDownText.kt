package com.ssafy.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.ssafy.designsystem.values.Black
import com.ssafy.designsystem.values.PaddingOne
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.White

@Composable
fun <T> DropDownText(
    modifier: Modifier = Modifier,
    title: String,
    dropdownList: List<T>,
    initItem: T,
    onItemChange: (T) -> Unit,
    textColor: Color = Black,
    dropdownItemToText: (T) -> String = { it -> it.toString() }
) {
    val (isExpanded, setExpanded) = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = PaddingOne)
    ) {

        Text(
            text = title,
            fontSize = TextSmall,
            color = Primary
        )

        Row(modifier = Modifier
            .clickable { setExpanded(true) }
            .padding(vertical = PaddingSmall)
        ) {
            Text(
                text = dropdownItemToText(initItem),
                fontSize = TextMedium,
                modifier = Modifier.weight(1f),
                color = textColor
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "dropdown"
            )
        }

        HorizontalDivider(color = Black)

        DropdownMenu(
            containerColor = White,
            expanded = isExpanded,
            onDismissRequest = { setExpanded(false) }
        ) {
            dropdownList.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = dropdownItemToText(item),
                            fontSize = TextMedium,
                            fontWeight = FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onItemChange(item)
                        setExpanded(false)
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun DropDownTextPreview() {
    DropDownText(
        title = "워크 스페이스",
        dropdownList = listOf("workspace1", "workspace2", "workspace3"),
        initItem = "workspace1",
        onItemChange = {}
    )
}