package com.ssafy.home.update

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.EditText
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.IconLegendLarge
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSemiLarge
import com.ssafy.designsystem.values.TextMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    modifier: Modifier = Modifier,
    title: String = "프로필 수정",
    onBackPressed: () -> Unit,
) {
    val (name, onValueChanged) = remember { mutableStateOf("") }

    val url =
        "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z"

    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = title, fontSize = TextMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.Black,
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            modifier = Modifier.size(IconMedium)
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(PaddingDefault),
            verticalArrangement = Arrangement.spacedBy(PaddingDefault),
        ) {
            Text(text = "프로필 사진", fontWeight = FontWeight.Bold, fontSize = TextMedium)

            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Box(
                    modifier = Modifier
                        .height(IconLegendLarge)
                        .clip(CircleShape)
                        .aspectRatio(1f)
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .height(IconLegendLarge)
                        .aspectRatio(1f)
                        .padding(PaddingXSemiLarge)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(IconLarge)
                            .shadow(elevation = PaddingSmall, shape = CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(PaddingSmall)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            modifier = Modifier
                                .size(IconMedium)
                                .align(Alignment.Center),
                            tint = Color.Black
                        )
                    }
                }
            }
            EditText(
                title = "닉네임",
                text = name,
                onTextChange = { onValueChanged(it) },
            )
            FilledButton(text = "수정하기", onClick = { /*TODO*/ })
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    UpdateProfileScreen(onBackPressed = {})
}