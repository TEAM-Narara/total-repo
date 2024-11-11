package com.ssafy.home.update

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.ssafy.model.user.User
import com.ssafy.ui.launcher.rememberLauncherForSaveImage
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun UpdateProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: UpdateViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.resetUiState()
        viewModel.getUser()
    }

    user?.let {
        UpdateProfileScreen(
            modifier = modifier,
            user = it,
            onBackPressed = onBackPressed,
            onChange = viewModel::change,
            onChangeFile = viewModel::changeProfileImage
        )
    } ?: LoadingScreen()

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateProfileScreen(
    modifier: Modifier = Modifier,
    user: User,
    title: String = "프로필 수정",
    onBackPressed: () -> Unit,
    onChange: (String) -> Unit,
    onChangeFile: (String) -> Unit,
) {
    val (name, onValueChanged) = remember(user.nickname) { mutableStateOf(user.nickname) }
    val imageLauncher = rememberLauncherForSaveImage(onChangeFile)

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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                        model = user.profileImgUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(Icons.Default.AccountCircle),
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
                            tint = Color.Black,
                            modifier = Modifier
                                .size(IconMedium)
                                .align(Alignment.Center)
                                .clickable { imageLauncher.launch("image/*") }
                        )
                    }
                }
            }
            EditText(
                title = "닉네임",
                text = name,
                onTextChange = onValueChanged,
            )
            FilledButton(text = "수정하기", onClick = { onChange(name) })
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    UpdateProfileScreen(onBackPressed = {})
}