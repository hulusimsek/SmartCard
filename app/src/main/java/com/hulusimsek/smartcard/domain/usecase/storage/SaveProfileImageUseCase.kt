package com.hulusimsek.smartcard.domain.usecase.storage

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class SaveProfileImageUseCase @Inject constructor (
    private val application: Application
) {

    suspend operator fun invoke(uri: Uri, oldUri: String?): String? = withContext(Dispatchers.IO) {
        try {
            // Mevcut bir dosya yolu ise doğrudan döndür
            if(uri == (oldUri?.toUri() ?: "")) {
                return@withContext oldUri
            }

            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(application.filesDir, fileName)

            application.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}