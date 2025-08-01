package com.hulusimsek.smartcard.presentation.home

import com.hulusimsek.smartcard.domain.model.NfcMode
import com.hulusimsek.smartcard.domain.model.NfcStatus
import com.hulusimsek.smartcard.domain.model.QrCodeData
import com.hulusimsek.smartcard.domain.model.QrScanResult
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val socialAccounts: List<SocialMediaAccount> = emptyList(),
    val activeSocialAccounts: List<SocialMediaAccount> = emptyList(),
    val errorMessage: String? = null,
    val isShowNfcWarning: Boolean = false,
    val isNfcSharingDialogVisible: Boolean = false,
    val isShowBottomSheetVisible: Boolean = false,
    val nfcStatus: NfcStatus = NfcStatus.DISABLED,
    val qrCodeData: QrCodeData? = null,
    val isGeneratingQrCode: Boolean = false,
    val qrCodeError: String? = null,

    val scannedQrData: String? = null,

    val nfcMode: NfcMode = NfcMode.LISTENER
)