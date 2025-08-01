package com.hulusimsek.smartcard.domain.usecase.nfc

import com.hulusimsek.smartcard.domain.model.NfcData
import com.hulusimsek.smartcard.domain.repository.NfcRepository
import javax.inject.Inject

class EnableNfcSenderUseCase @Inject constructor(
    private val nfcRepository: NfcRepository
) {
    suspend operator fun invoke(jsonData: String): Result<Unit> {
        return nfcRepository.enableSenderMode(jsonData)
    }
}