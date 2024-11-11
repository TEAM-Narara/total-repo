package com.ssafy.network.util

import android.content.Context
import android.graphics.Bitmap
import com.amazonaws.HttpMethod
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.math.sqrt

@Singleton
class ImageUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transferUtility: TransferUtility,
    private val s3Client: AmazonS3Client
) {
    private val metadata = ObjectMetadata().apply { contentType = "image/webp" }

    suspend fun uploadFile(
        key: String,
        file: File,
        isImage: Boolean,
    ): Boolean = suspendCancellableCoroutine { continuation ->

        val observer = if (isImage) {
            transferUtility.upload(BUCKET_NAME, key, file, metadata)
        } else {
            transferUtility.upload(BUCKET_NAME, key, file)
        }

        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) continuation.resume(true)
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                continuation.resume(false)
            }
        })
    }


    fun rescaleBitmap(bitmap: Bitmap): Bitmap {
        val targetSize = 2 * 1024 * 1024
        val currentSize = bitmap.width * bitmap.height * 4
        if (currentSize <= targetSize) return bitmap
        val scale = sqrt(targetSize.toFloat() / currentSize)

        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()

        return Bitmap.createScaledBitmap(
            bitmap,
            newWidth,
            newHeight,
            true
        ).also {
            if (it != bitmap) {
                bitmap.recycle()
            }
        }
    }

    fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(
            context.cacheDir,
            "profile_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
        }

        return file
    }


    fun downloadFile(key: String): String {
        val internalDir = context.filesDir
        val date = Date().apply {
            time += 1000 * 3600
        }

        val generatedUrlRequest = GeneratePresignedUrlRequest(BUCKET_NAME, key)
            .withMethod(HttpMethod.GET)
            .withExpiration(date)

        val url = s3Client.generatePresignedUrl(generatedUrlRequest).toString()
        val file = File(internalDir, key.split("/").last())

        URL(url).openStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file.path
    }

    companion object {
        const val BUCKET_NAME = "superboard-bucket"
    }
}
