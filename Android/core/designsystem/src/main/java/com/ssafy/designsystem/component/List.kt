package com.ssafy.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.ssafy.designsystem.R
import com.ssafy.designsystem.values.ElevationDefault
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.ListWidth
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.SpacerMedium
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@Composable
fun List(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    isWatching: Boolean = false,
    addCard: () -> Unit,
    addPhoto: () -> Unit,
    cardList: List<Any> = emptyList(),
    maxTitleLength: Int = 15,
) {
    val (value, onValueChange) = remember { mutableStateOf(title) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier.width(ListWidth),
        colors = CardDefaults.cardColors().copy(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ElevationDefault
        )
    ) {
        Column(
            modifier = Modifier
                .padding(PaddingDefault)
                .weight(1f, false)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.length <= maxTitleLength) onValueChange(newValue)
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = TextMedium),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onTitleChange(value)
                        }
                    )
                )

                if (isWatching) {
                    Image(
                        modifier = Modifier.size(IconMedium),
                        painter = painterResource(id = R.drawable.watch),
                        contentDescription = "watch"
                    )
                }
            }

            LazyColumn {
                items(cardList) { card ->
                    Card(card)
                }
            }

            Spacer(modifier = Modifier.height(SpacerMedium))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(modifier = Modifier
                    .clickable { addCard() }
                    .weight(1f)
                ) {
                    Text(
                        text = "+ Add Card", fontSize = TextMedium,
                        modifier = Modifier.padding(vertical = PaddingSmall)
                    )
                }

                Box(modifier = Modifier.clickable { addPhoto() }) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "이미지 추가",
                        modifier = Modifier.padding(PaddingSmall)
                    )
                }
            }
        }
    }
}
