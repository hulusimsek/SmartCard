package com.hulusimsek.smartcard.domain.usecase.nfc

import com.hulusimsek.smartcard.domain.repository.NfcRepository
import javax.inject.Inject

class ResumeNfcAllUseCase @Inject constructor(
    private val nfcRepository: NfcRepository
) {
    suspend operator fun invoke() {
        nfcRepository.onResume()
    }
}