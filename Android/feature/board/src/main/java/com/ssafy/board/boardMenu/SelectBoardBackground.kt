package com.ssafy.board.boardMenu

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.sharp.Add
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ssafy.designsystem.getContrastingTextColor
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.LightSkyBlue
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingTwo
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.backgroundColorList
import com.ssafy.designsystem.values.toColor
import com.ssafy.designsystem.values.toColorString
import com.ssafy.model.background.Cover
import com.ssafy.model.with.CoverType
import com.ssafy.ui.launcher.rememberLauncherForSaveImage
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Composable
fun SelectBoardBackgroundScreen(
    onBackPressed: (Cover?) -> Unit,
    viewModel: SelectBoardBackgroundViewModel = hiltViewModel(),
    selectedCover: Cover?,
    boardId: Long?
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imagePathList by viewModel.imagePathList.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.resetUiState()
        viewModel.loadMemberBackgrounds()
    }

    SelectBoardBackgroundScreen(
        onBackPressed = { onBackPressed(selectedCover) },
        selectedCover = selectedCover,
        imagePathList = imagePathList,
        addImagePath = viewModel::addImagePath,
        onCoverSelected = { viewModel.coverSelect(boardId, it, onBackPressed) }
    )

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectBoardBackgroundScreen(
    onBackPressed: () -> Unit,
    selectedCover: Cover?,
    imagePathList: List<String>,
    addImagePath: (String) -> Unit,
    onCoverSelected: (Cover) -> Unit
) {
    val attachmentLauncher = rememberLauncherForSaveImage(addImagePath)

    Scaffold(modifier = Modifier.background(Color.White),
        topBar = {
            TopAppBar(
                title = { Text(text = "보드 배경 변경", fontSize = TextMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.Black,
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.Clear,
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
            Text(text = "색상", fontSize = TextMedium, fontWeight = FontWeight.SemiBold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5), modifier = Modifier.fillMaxWidth(),
            ) {
                items(backgroundColorList.size) {
                    val color = backgroundColorList[it]
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(PaddingTwo, PaddingTwo)
                            .background(color = color, shape = RoundedCornerShape(PaddingSmall))
                            .clickable {
                                val cover = Cover(CoverType.COLOR, color.toColorString())
                                onCoverSelected(cover)
                            }
                            .then(
                                if (isSameColor(selectedCover, color)) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = getContrastingTextColor(selectedCover.value.toColor()),
                                        shape = RoundedCornerShape(PaddingSmall)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        if (isSameColor(selectedCover, color)) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = getContrastingTextColor(selectedCover.value.toColor())
                            )
                        }
                    }
                }
            }
            Text(text = "이미지", fontSize = TextMedium, fontWeight = FontWeight.SemiBold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), modifier = Modifier.fillMaxWidth(),
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(PaddingTwo, PaddingTwo)
                            .background(LightSkyBlue, shape = RoundedCornerShape(PaddingSmall))
                            .clickable {
                                attachmentLauncher.launch("image/*")
                            },
                    )
                    {
                        Icon(
                            imageVector = Icons.Sharp.Add,
                            contentDescription = "색상 추가",
                            modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.Center),
                            tint = Primary
                        )
                    }
                }
                items(imagePathList.size) {
                    val image = imagePathList[it]
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(PaddingTwo, PaddingTwo)
                            .clickable {
                                val cover = Cover(CoverType.IMAGE, image)
                                onCoverSelected(cover)
                            }
                            .then(
                                if (isSameImage(selectedCover, image)) Modifier.border(
                                    width = 2.dp,
                                    color = getContrastingTextColor(Color.Black),
                                    shape = RoundedCornerShape(PaddingSmall)
                                ) else Modifier
                            ),
                    )
                    {
                        if (isSameImage(selectedCover, image)) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = getContrastingTextColor(
                                    Color.Black
                                )
                            )
                        }
                        DisplayImageFromPath(image)
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayImageFromPath(imagePath: String) {
    val uri = Uri.fromFile(File(imagePath))
    AsyncImage(
        model = uri,
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(PaddingSmall)),
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalContracts::class)
private fun isSameColor(selectedCover: Cover?, color: Color): Boolean {
    contract {
        returns(true) implies (selectedCover != null)
    }

    return selectedCover?.type == CoverType.COLOR && selectedCover.value.toColor() == color
}

@OptIn(ExperimentalContracts::class)
private fun isSameImage(selectedCover: Cover?, image: String): Boolean {
    contract {
        returns(true) implies (selectedCover != null)
    }

    return selectedCover?.type == CoverType.IMAGE && selectedCover.value == image
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview2() {
    SelectBoardBackgroundScreen(
        {}, selectedCover = null, listOf(), {}, {}
    )
}
