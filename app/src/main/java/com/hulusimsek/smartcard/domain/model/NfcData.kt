package com.hulusimsek.smartcard.domain.model

data class NfcData(
    val jsonContent: String,
    val timestamp: Long = System.currentTimeMillis()
)