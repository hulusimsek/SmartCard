package com.hulusimsek.smartcard.data.utils

import android.content.Context
import android.net.Uri
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriHelper @Inject constructor() {

    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_qr_image_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun cleanupTempFiles(context: Context) {
        try {
            val cacheDir = context.cacheDir
            val tempFiles = cacheDir.listFiles { file ->
                file.name.startsWith("temp_qr_image_")
            }
            tempFiles?.forEach { it.delete() }
        } catch (e: Exception) {
            // Log error if needed
        }
    }
}