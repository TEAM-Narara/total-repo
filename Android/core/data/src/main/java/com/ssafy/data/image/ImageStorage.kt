package com.ssafy.data.image

import com.ssafy.data.di.IoDispatcher
import com.ssafy.network.util.S3ImageUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorage @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val s3ImageUtil: S3ImageUtil,
) {

    suspend fun saveAll(key: String?, onDoAction: suspend (String?) -> Unit) = coroutineScope {
        launch(ioDispatcher) {
            if (key.isNullOrBlank()) {
                onDoAction(null)
                return@launch
            }

            listOf(
                async { save("${key}-${S3ImageUtil.MINI}") },
                async { save(key) }
            ).forEach { deferred ->
                onDoAction(deferred.await())
            }
        }
    }

    private fun save(key: String): String = runCatching {
        s3ImageUtil.downloadFile(key)
    }.fold(
        onSuccess = { it },
        onFailure = { "" }
    )

    fun get(path: String): File? = File(path).takeIf { it.exists() && it.isFile }

    fun getAll(): List<File> =
        s3ImageUtil.imageDirectory.listFiles()?.filter { it.isFile } ?: emptyList()

    fun delete(path: String): Boolean = File(path).delete()

    fun deleteAllImages(): Boolean = s3ImageUtil.imageDirectory.deleteRecursively()

}