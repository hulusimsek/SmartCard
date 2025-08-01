package com.hulusimsek.smartcard.presentation.home

sealed class NfcUiEvent {
    object ShowNfcUnavailable : NfcUiEvent()
    object ShowNfcSettingsDialog : NfcUiEvent()
    object NfcReady : NfcUiEvent()
}