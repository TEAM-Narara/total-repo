package com.ssafy.board.boardMenu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ssafy.board.getContrastingTextColor
import com.ssafy.designsystem.values.IconMedium
import com.ssafy.designsystem.values.LightSkyBlue
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingTwo
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.model.background.BackgroundDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBoardBackground(
    onBackPressed: () -> Unit,
    selectedBackground: BackgroundDto,
) {
    val colors = listOf(
        0xFFFCFCFC,
        0xFFFFE3E8,
        0xFFFFF7BD,
        0xFFD9E1F4,
        0xFFE5EFFF,
        0xFFEAFFE5,
        0xFFEEE5FF,
        0xFFCCCCCC
    )
    val localImages = remember { mutableStateListOf<String>() }

    val attachmentLauncher = rememberLauncherForSaveImage { path ->
        localImages.add(path)
    }
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
                items(colors.size) {
                    val color = colors[it]
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(PaddingTwo, PaddingTwo)
                            .background(Color(color), shape = RoundedCornerShape(PaddingSmall))
                            .clickable {
                                // TODO: Viewmodel에 색상 바꿔줘.
                            }
                            .then(
                                if (selectedBackground.imgPath == null && selectedBackground.color == color) Modifier.border(
                                    width = 2.dp,
                                    color = getContrastingTextColor(
                                        Color(selectedBackground.color)
                                    ),
                                    shape = RoundedCornerShape(PaddingSmall)
                                ) else Modifier
                            ),
                    )
                    {
                        if (selectedBackground.imgPath == null && selectedBackground.color == color) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = getContrastingTextColor(
                                    Color(selectedBackground.color)
                                )
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
                items(localImages.size) {
                    val image = localImages[it]
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(PaddingTwo, PaddingTwo)
                            .clickable {
                                // TODO: Viewmodel에 selectedBackground 이미지 url 바꿔줘.
                            }
                            .then(
                                if (selectedBackground.imgPath != null && selectedBackground.imgPath == image) Modifier.border(
                                    width = 2.dp,
                                    color = getContrastingTextColor(
                                        Color.Black
                                    ),
                                    shape = RoundedCornerShape(PaddingSmall)
                                ) else Modifier
                            ),
                    )
                    {
                        if (selectedBackground.imgPath != null && selectedBackground.imgPath == image) {
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

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview2() {
    SelectBoardBackground(
        {}, BackgroundDto(0xFFFCFCFC, null)
    )
}

@Composable
fun rememberLauncherForSaveImage(saveAttachment: (String) -> Unit): ManagedActivityResultLauncher<String, Uri?> {
    val contract = ActivityResultContracts.GetContent()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    return rememberLauncherForActivityResult(contract = contract) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        coroutineScope.launch(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val imageData = inputStream.readBytes()

                val exif = ExifInterface(ByteArrayInputStream(imageData))
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                val matrix = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> Matrix().apply { postRotate(90f) }
                    ExifInterface.ORIENTATION_ROTATE_180 -> Matrix().apply { postRotate(180f) }
                    ExifInterface.ORIENTATION_ROTATE_270 -> Matrix().apply { postRotate(270f) }
                    else -> Matrix()
                }

                val rotatedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                )

                val file =
                    File(context.cacheDir, "uploaded_image_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                }

                saveAttachment(file.absolutePath)
            }
        }
    }
}