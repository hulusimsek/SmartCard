package com.hulusimsek.smartcard.domain.usecase.storage

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class CreateTempImageUriUseCase {
    operator fun invoke(context: Context): Uri? {
        return try {
            val tempProfileDir = File(context.cacheDir, "temp_profile")
            if (!tempProfileDir.exists()) {
                tempProfileDir.mkdirs()
            }

            val tempImageFile = File(tempProfileDir, "temp_profile_${System.currentTimeMillis()}.jpg")

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempImageFile
            )
        } catch (e: Exception) {
            null
        }
    }
}