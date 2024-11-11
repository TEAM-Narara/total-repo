package com.ssafy.data.image

import android.content.Context
import com.ssafy.data.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorage @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) {
    private val imageDirectory: File
        get() = File(context.filesDir, "images").apply {
            if (!exists()) mkdirs()
        }

    suspend fun save(
        imageUrl: String,
        fileName: String = "IMG_${getTimeStamp()}.jpg"
    ): String = withContext(ioDispatcher) {
        val file = File(imageDirectory, fileName)

        URL(imageUrl).openStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        file.absolutePath
    }

    fun get(path: String): File? = File(path).takeIf { it.exists() && it.isFile }

    fun getAll(): List<File> = imageDirectory.listFiles()?.filter { it.isFile } ?: emptyList()

    fun delete(path: String): Boolean = File(path).delete()

    fun deleteAllImages(): Boolean = imageDirectory.deleteRecursively()

    private fun getTimeStamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }
}