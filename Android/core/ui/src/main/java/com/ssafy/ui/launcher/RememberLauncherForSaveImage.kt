package com.ssafy.ui.launcher

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

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
