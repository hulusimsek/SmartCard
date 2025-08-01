package com.hulusimsek.smartcard.domain.repository

import com.hulusimsek.smartcard.domain.model.ImageSource
import com.hulusimsek.smartcard.domain.model.QrCodeData
import com.hulusimsek.smartcard.domain.model.QrScanResult
import kotlinx.coroutines.flow.Flow

interface QrCodeRepository {
    suspend fun generateQrCode(data: String): QrCodeData
    fun extractDataFromQrResult(rawValue: String): String?
}