package com.hulusimsek.smartcard.domain.usecase.nfc

import com.hulusimsek.smartcard.domain.model.NfcData
import com.hulusimsek.smartcard.domain.repository.NfcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ObserveNfcDataUseCase @Inject constructor(
    private val repository: NfcRepository
) {
    operator fun invoke(): SharedFlow<NfcData> {
        return repository.nfcDataFlow
    }
}