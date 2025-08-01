package com.hulusimsek.smartcard.domain.usecase.qrcode

import com.hulusimsek.smartcard.domain.model.QrCodeData
import com.hulusimsek.smartcard.domain.repository.QrCodeRepository
import javax.inject.Inject

class GenerateQrCodeUse @Inject constructor(
    private val qrCodeRepository: QrCodeRepository
) {
    suspend operator fun invoke(jsonData: String): Result<QrCodeData> {
        return try {
            val qrCodeData = qrCodeRepository.generateQrCode(jsonData)
            Result.success(qrCodeData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}