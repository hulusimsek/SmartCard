package com.hulusimsek.smartcard.data.repository

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.core.net.toUri
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.hulusimsek.smartcard.data.utils.UriHelper
import com.hulusimsek.smartcard.domain.model.QrCodeData
import com.hulusimsek.smartcard.domain.model.QrScanResult
import com.hulusimsek.smartcard.domain.repository.QrCodeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.net.URLEncoder
import javax.inject.Inject

class QrCodeRepositoryImpl @Inject constructor(
) : QrCodeRepository {

    private val _cameraPreview = MutableSharedFlow<ByteArray>()

    override suspend fun generateQrCode(data: String): QrCodeData {
        return withContext(Dispatchers.IO) {
            try {
                val writer = QRCodeWriter()
                val hints = mapOf(
                    EncodeHintType.CHARACTER_SET to "UTF-8"
                )
                // Sadece data kısmını encode edin, tüm URI'yi değil
                val encodedData = URLEncoder.encode(data, "UTF-8")
                val qrContent = "https://hulusimsek.github.io/smart-card/index.html?data=$encodedData"

                val bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 512, 512, hints)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val pixels = IntArray(width * height)

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val pixelIndex = y * width + x
                        pixels[pixelIndex] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    }
                }

                QrCodeData(width, height, pixels)
            } catch (e: Exception) {
                throw Exception(e.message)
            }
        }
    }


    override fun extractDataFromQrResult(rawValue: String): String? {
        return try {
            val uri = rawValue.toUri()
            //uri.getQueryParameter("data")
            val encoded = uri.getQueryParameter("data")
            encoded?.let { URLDecoder.decode(it, "UTF-8") }
        } catch (e: Exception) {
            null
        }
    }

}