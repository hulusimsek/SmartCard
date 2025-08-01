package com.hulusimsek.smartcard.domain.model

sealed class QrScanResult {
    data class Success(val data: String) : QrScanResult()
    data class Error(val message: String) : QrScanResult()
    object InvalidFormat : QrScanResult()
}