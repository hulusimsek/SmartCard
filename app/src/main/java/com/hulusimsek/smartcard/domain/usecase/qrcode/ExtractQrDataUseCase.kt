package com.hulusimsek.smartcard.domain.usecase.qrcode

import com.hulusimsek.smartcard.domain.repository.QrCodeRepository
import javax.inject.Inject


class ExtractQrDataUseCase @Inject constructor(private val repository: QrCodeRepository) {
    operator fun invoke(rawValue: String): String? {
        return repository.extractDataFromQrResult(rawValue)
    }
}