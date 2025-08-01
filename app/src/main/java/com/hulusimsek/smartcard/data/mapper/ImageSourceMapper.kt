package com.hulusimsek.smartcard.data.mapper

import android.content.Context
import android.net.Uri
import com.hulusimsek.smartcard.data.utils.UriHelper
import com.hulusimsek.smartcard.domain.model.ImageSource
import javax.inject.Inject

class ImageSourceMapper @Inject constructor(
    private val uriHelper: UriHelper
) {

    suspend fun fromUri(context: Context, uri: Uri): ImageSource {
        return try {
            // URI'yi string olarak kaydet
            ImageSource(uri = uri.toString())
        } catch (e: Exception) {
            throw Exception("URI dönüştürülemedi: ${e.message}")
        }
    }

    suspend fun fromPath(path: String): ImageSource {
        return ImageSource(path = path)
    }

    suspend fun fromBytes(bytes: ByteArray): ImageSource {
        return ImageSource(bytes = bytes)
    }
}