package com.hulusimsek.smartcard.domain.model

enum class NfcMode {
    LISTENER,    // Reader mode aktif, HCE pasif
    SENDER,       // HCE aktif, Reader mode pasif
    DISABLE
}