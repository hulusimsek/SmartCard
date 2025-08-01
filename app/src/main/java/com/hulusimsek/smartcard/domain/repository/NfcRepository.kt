package com.hulusimsek.smartcard.domain.repository

import com.hulusimsek.smartcard.domain.model.NfcData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface NfcRepository {
    val nfcDataFlow: SharedFlow<NfcData>
    suspend fun enableListenerMode() : Result<Unit>
    suspend fun enableSenderMode(jsonData: String) : Result<Unit>
    suspend fun disableNfcAll() : Result<Unit>
    suspend fun onResume()
    suspend fun onPause()
    suspend fun isNfcEnabled(): Boolean
    suspend fun isNfcAvailable(): Boolean
}