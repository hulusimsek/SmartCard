package com.hulusimsek.smartcard.domain.usecase.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


class DeleteProfileImageUseCase {
    suspend operator fun invoke(path: String?): Boolean = withContext(Dispatchers.IO) {
        if (path.isNullOrEmpty()) return@withContext false

        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}