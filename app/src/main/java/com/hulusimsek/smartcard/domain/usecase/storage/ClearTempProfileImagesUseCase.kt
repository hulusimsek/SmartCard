package com.hulusimsek.smartcard.domain.usecase.storage

import android.content.Context
import java.io.File

class ClearTempProfileImagesUseCase {
    operator fun invoke(context: Context) {
        try {
            val tempDir = File(context.cacheDir, "temp_profile")
            if (tempDir.exists() && tempDir.isDirectory) {
                tempDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith("temp_profile_")) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
        }
    }
}