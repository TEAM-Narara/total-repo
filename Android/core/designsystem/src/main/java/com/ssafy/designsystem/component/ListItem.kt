package com.ssafy.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.R
import com.ssafy.designsystem.values.CornerMedium
import com.ssafy.designsystem.values.ElevationDefault
import com.ssafy.designsystem.values.ElevationLarge
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.ListWidth
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.White

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    isWatching: Boolean = false,
    addCard: (String) -> Unit,
    addPhoto: () -> Unit,
    maxTitleLength: Int = 15,
    cardList: @Composable () -> Unit = {},
) {
    Card(
        modifier = modifier.width(ListWidth),
        shape = RoundedCornerShape(CornerMedium),
        colors = CardDefaults.cardColors().copy(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ElevationDefault
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(PaddingDefault)
                .weight(1f, false),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EditableText(
                    text = title,
                    onInputFinished = onTitleChange,
                    maxTitleLength = maxTitleLength
                )

                Spacer(modifier = Modifier.weight(1f))

                if (isWatching) {
                    Image(
                        modifier = Modifier.size(IconMedium),
                        painter = painterResource(id = R.drawable.watch),
                        contentDescription = "watch",
                    )
                }
            }

            Box(modifier = Modifier.weight(1f, fill = false)) {
                cardList()
            }

            AddCardButton(addCard = addCard)
        }
    }
}

@Composable
fun AddCardButton(modifier: Modifier = Modifier, addCard: (String) -> Unit = {}) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocused) {
        if (isFocused) focusRequester.requestFocus()
    }

    if (!isFocused) Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.clickable { isFocused = true }) {
            Text(
                text = "+ Add Card", fontSize = TextMedium,
                modifier = Modifier.padding(vertical = PaddingSmall)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.clickable { isFocused = true }) {
            Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = "이미지 추가",
                modifier = Modifier.padding(PaddingSmall)
            )
        }
    } else {
        Card(
            shape = RoundedCornerShape(CornerMedium),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = ElevationDefault
            ),
        ) {
            EditableText(
                modifier = Modifier
                    .padding(vertical = PaddingDefault, horizontal = PaddingMedium)
                    .focusRequester(focusRequester),
                onInputFinished = {
                    isFocused = false
                    addCard(it)
                }
            )
        }
    }
}

@Preview
@Composable
private fun ListItemPreview() {
    ListItem(
        title = "title",
        isWatching = true,
        onTitleChange = {},
        addCard = {},
        addPhoto = {},
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(PaddingMedium)) {
            item { Spacer(modifier = Modifier.height(PaddingXSmall)) }
            items(3) {
                CardPreview()
            }
            item { Spacer(modifier = Modifier.height(PaddingXSmall)) }
        }
    }
}