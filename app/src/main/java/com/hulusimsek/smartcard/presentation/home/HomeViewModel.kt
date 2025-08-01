package com.hulusimsek.smartcard.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.smartcard.domain.model.NfcMode
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.usecase.SocialMediaUseCases
import com.hulusimsek.smartcard.domain.usecase.UserUseCases
import com.hulusimsek.smartcard.domain.usecase.composite.JsonConverterUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.DisableNfcAllUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.EnableNfcListenerUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.EnableNfcSenderUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.IsNfcAvailableUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.IsNfcEnabledUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.ObserveNfcDataUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.ResumeNfcAllUseCase
import com.hulusimsek.smartcard.core.util.Constants
import com.hulusimsek.smartcard.core.util.TimeUtils
import com.hulusimsek.smartcard.domain.model.NfcStatus
import com.hulusimsek.smartcard.domain.provider.ActivityProvider
import com.hulusimsek.smartcard.domain.usecase.qrcode.ExtractQrDataUseCase
import com.hulusimsek.smartcard.domain.usecase.qrcode.GenerateQrCodeUse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val socialMediaUseCases: SocialMediaUseCases,
    private val enableNfcListenerUseCase: EnableNfcListenerUseCase,
    private val enableNfcSenderUseCase: EnableNfcSenderUseCase,
    private val observeNfcDataUseCase: ObserveNfcDataUseCase,
    private val disableNfcAllUseCase: DisableNfcAllUseCase,
    private val resumeNfcAllUseCase: ResumeNfcAllUseCase,
    private val isNfcEnabledUseCase: IsNfcEnabledUseCase,
    private val isNfcAvailableUseCase: IsNfcAvailableUseCase,
    private val generateQrCodeUseCase: GenerateQrCodeUse,
    private val extractQrDataUseCase: ExtractQrDataUseCase,
    private val activityProvider: ActivityProvider,
    private val jsonConvertUseCase: JsonConverterUseCase
) : ViewModel() {

    // Kullanıcı bilgileri için StateFlow


    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<NfcUiEvent>(replay = 1)
    val uiEvent: SharedFlow<NfcUiEvent> = _uiEvent

    private val _nfcData = MutableStateFlow("")
    val nfcData: StateFlow<String> = _nfcData

    private val _deepLinkData = MutableStateFlow<String?>(null)
    val deepLinkData = _deepLinkData.asStateFlow()

    init {
        // Uygulama başlatıldığında kullanıcının var olup olmadığını kontrol et
        ensureUserExists()

        // Kullanıcıyı ve hesaplarını izlemeye başla
        observeUser()
        observeSocialAccounts()

        observeNfcData()
    }

    private fun observeNfcData() {
        viewModelScope.launch {
            observeNfcDataUseCase().collect { nfcData ->
                // Navigate to NFC data screen - event olarak emit edilebilir
                _nfcData.value = nfcData.jsonContent
                Log.d("nfcdeneme", "Homeviewmodel NFC data received: ${nfcData.jsonContent}")
            }
        }
    }

    private suspend fun enableListenerMode() {
        _uiState.update { it.copy(nfcMode = NfcMode.LISTENER) }
        enableNfcListenerUseCase()
    }

    private suspend fun enableSenderMode(jsonData: String) {
        _uiState.update { it.copy(nfcMode = NfcMode.SENDER) }
        enableNfcSenderUseCase(jsonData)
    }

    fun dismissNfcDialog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isNfcSharingDialogVisible = false) }
            enableListenerMode()
        }
    }

    fun disableNfcAll() {
        viewModelScope.launch {
            disableNfcAllUseCase()
        }
    }

    fun setCurrentActivity(activity: Any) {
        activityProvider.setCurrentActivity(activity)
    }

    fun resumeNfc() {
        viewModelScope.launch {
            resumeNfcAllUseCase()
        }
    }

    fun checkNfcStatus() {
        viewModelScope.launch {
            if (!isNfcAvailableUseCase()) {
                if(!uiState.value.isShowNfcWarning) {
                    _uiEvent.emit(NfcUiEvent.ShowNfcUnavailable)
                    _uiState.update { it.copy(isShowNfcWarning = true) }
                }
                _uiState.update { it.copy(nfcStatus = NfcStatus.NOT_SUPPORTED) }
                return@launch
            }
            if (!isNfcEnabledUseCase()) {
                if(!uiState.value.isShowNfcWarning) {
                    _uiEvent.emit(NfcUiEvent.ShowNfcSettingsDialog)
                    _uiState.update { it.copy(isShowNfcWarning = true) }
                }
                _uiState.update { it.copy(nfcStatus = NfcStatus.DISABLED) }
                return@launch
            }

            _uiEvent.emit(NfcUiEvent.NfcReady)
            _uiState.update { it.copy(nfcStatus = NfcStatus.ENABLED) }
        }
    }

    fun clearNfcData() {
        _nfcData.value = ""
    }




    fun onSocialMediaLinkClicked(account: SocialMediaAccount) {
        viewModelScope.launch {
            socialMediaUseCases.toggleAccountActive(account)
        }
    }

    private fun convertUserDataToJson(): String? {

        uiState.value.user?.let { user ->
            val json = jsonConvertUseCase.convertUserProfileToJsonUseCase(
                user, uiState.value.activeSocialAccounts
            )
            return json
        }
        return null
    }

    fun sendUserDataNfc() {
        viewModelScope.launch {
            if (_uiState.value.nfcStatus == NfcStatus.DISABLED) {
                _uiEvent.emit(NfcUiEvent.ShowNfcSettingsDialog)
                return@launch
            }
            else if (_uiState.value.nfcStatus == NfcStatus.NOT_SUPPORTED) {
                _uiEvent.emit(NfcUiEvent.ShowNfcUnavailable)
                return@launch
            }
            convertUserDataToJson()?.let { json ->
                _uiState.update {
                    it.copy(
                        nfcMode = NfcMode.SENDER,
                        isNfcSharingDialogVisible = true
                    )
                }
                enableSenderMode(json)
            }
        }
    }

    fun showBottomSheetVisible() {
        _uiState.update { it.copy(isShowBottomSheetVisible = true) }
    }

    fun dismissShowBottomSheet() {
        viewModelScope.launch {
            _uiState.update { it.copy(isShowBottomSheetVisible = false) }
        }
    }

    fun toggleNfc(enabled: Boolean) {
        viewModelScope.launch {
            if (_uiState.value.nfcStatus == NfcStatus.DISABLED) {
                _uiEvent.emit(NfcUiEvent.ShowNfcSettingsDialog)
                return@launch
            }
            else if (_uiState.value.nfcStatus == NfcStatus.NOT_SUPPORTED) {
                _uiEvent.emit(NfcUiEvent.ShowNfcUnavailable)
                return@launch
            }
            else if (enabled) {
                _uiState.update {
                    it.copy(
                        nfcMode = NfcMode.LISTENER,
                    )
                }
                enableNfcListenerUseCase()
            } else {
                _uiState.update {
                    it.copy(
                        nfcMode = NfcMode.DISABLE
                    )
                }
                disableNfcAll()
            }
        }
    }

    fun generateQrCodeForUserData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isGeneratingQrCode = true, qrCodeError = null) }

                val jsonData = convertUserDataToJson()
                if (jsonData == null) {
                    _uiState.update {
                        it.copy(
                            isGeneratingQrCode = false,
                            qrCodeError = "Kullanıcı verisi bulunamadı"
                        )
                    }
                    return@launch
                }

                generateQrCodeUseCase(jsonData)
                    .onSuccess { qrCodeData ->
                        _uiState.update {
                            it.copy(
                                qrCodeData = qrCodeData,
                                isGeneratingQrCode = false,
                                qrCodeError = null
                            )
                        }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isGeneratingQrCode = false,
                                qrCodeError = exception.message ?: "QR kod oluşturulamadı"
                            )
                        }
                    }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGeneratingQrCode = false,
                        qrCodeError = "Beklenmeyen bir hata oluştu: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearQrCode() {
        _uiState.update { it.copy(qrCodeData = null, qrCodeError = null) }
    }

    fun handleAppLink(data: String) {
        _deepLinkData.value = data
    }

    fun clearDeepLinkData() {
        _deepLinkData.value = null
    }

    fun onQrScanned(rawValue: String) {
        val data = extractQrDataUseCase(rawValue)
        data?.let { qrData ->
            Log.e("qrtarayıcıdeneme", qrData)
            _nfcData.value = qrData
            _uiState.update { it.copy(scannedQrData = qrData) }
        }
    }



    // Kullanıcıyı veritabanında kontrol et, yoksa yeni bir kullanıcı oluştur
    private fun ensureUserExists() {
        viewModelScope.launch {
            val user = userUseCases.getUserById(Constants.DEFAULT_USER_ID)
            if (user == null) {
                val newUser = User(
                    id = Constants.DEFAULT_USER_ID,
                    firstName = null,
                    lastName = null,
                    createdAt = TimeUtils.getCurrentDateTimeString(),
                    profileImagePath = null
                )
                userUseCases.insertUser(newUser)
            }
        }
    }

    // Kullanıcı bilgilerini dinleyip StateFlow'a aktar
    private fun observeUser() {
        viewModelScope.launch {
            userUseCases.getUserStream(Constants.DEFAULT_USER_ID).collect { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user
                    )
                }
            }
        }
    }

    // Kullanıcının sosyal medya hesaplarını dinleyip StateFlow'a aktar
    private fun observeSocialAccounts() {
        viewModelScope.launch {
            socialMediaUseCases.getAccountStream(Constants.DEFAULT_USER_ID).collect { accounts ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        socialAccounts = accounts,
                        activeSocialAccounts = accounts.filter { it.isActive }
                    )
                }
            }
        }
    }
}