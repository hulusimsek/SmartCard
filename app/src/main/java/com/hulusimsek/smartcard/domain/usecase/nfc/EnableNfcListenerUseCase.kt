package com.hulusimsek.smartcard.domain.usecase.nfc

import com.hulusimsek.smartcard.domain.repository.NfcRepository
import javax.inject.Inject

class EnableNfcListenerUseCase @Inject constructor(
    private val nfcRepository: NfcRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return nfcRepository.enableListenerMode()
    }
}
