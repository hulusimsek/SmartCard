package com.hulusimsek.smartcard.presentation.social_media_account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.smartcard.domain.model.Countries
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.domain.usecase.composite.JsonConverterUseCase
import com.hulusimsek.smartcard.domain.usecase.composite.ParsePhoneNumberUseCase
import com.hulusimsek.smartcard.domain.usecase.local.GetDeviceLocaleUseCase
import com.hulusimsek.smartcard.core.util.Constants
import com.hulusimsek.smartcard.core.extension.clearPlus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialMediaAccountViewModel @Inject constructor(
    private val jsonConvertUseCase: JsonConverterUseCase,
    private val getDeviceLocaleUseCase: GetDeviceLocaleUseCase,
    private val parsePhoneNumberUseCase: ParsePhoneNumberUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    private val _currentAccount = MutableStateFlow<SocialMediaAccount?>(null)
    val currentAccount: StateFlow<SocialMediaAccount?> = _currentAccount

    private val _navigateToEditScreen = MutableSharedFlow<String>()
    val navigateToEditScreen = _navigateToEditScreen.asSharedFlow()

    private val _countries = MutableStateFlow<Countries>(Countries.TR)
    val country = _countries

    init {
        _countries.value = Countries.fromCountryCode(getDeviceLocaleUseCase().uppercase())
        Log.e("telefondeneme", getDeviceLocaleUseCase())
        Log.e("telefondeneme", _countries.value.toString())
    }


    fun onPlatformSelected(platform: SocialPlatform) {
        _uiState.update { it.copy(platform = platform) }
    }

    fun onUsernameChanged(username: String) {
        val result = parsePhoneNumberUseCase("${country.value.phoneCode}$username")
        val isPhoneValid = result.first != null
        Log.e("telefondeneme", result.toString())

        _uiState.update {
            it.copy(
                username = username,
                isPhoneValid = isPhoneValid
            )
        }

        //_uiState.update { it.copy(username = username) }
    }

    fun countryCodeChanged(countryCode: String) {

        _countries.value = Countries.fromPhoneCode(countryCode.clearPlus())
        val (code, iso, nationalNumber) = parsePhoneNumberUseCase("${_countries.value.phoneCode}${_uiState.value.username}")
        val isPhoneValid = code != null
        _uiState.update {
            it.copy(
                isPhoneValid = isPhoneValid
            )
        }
        Log.e("telefondeneme", _countries.value.toString())
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    private fun loadFromAccount(account: SocialMediaAccount?) {
        _currentAccount.value = account
        account?.let {
            if (account.platform.isPhone) {
                val (code, iso, nationalNumber) = parsePhoneNumberUseCase(account.username)
                val isPhoneValid = code != null
                _countries.value = Countries.fromPhoneCode("${code?.clearPlus()}$iso")
                _uiState.value = AccountUiState(
                    platform = it.platform,
                    username = nationalNumber ?: it.username,
                    isPhoneValid =isPhoneValid,
                    description = it.description ?: ""
                )
                return
            }
            _uiState.value = AccountUiState(
                platform = it.platform,
                username = it.username,
                description = it.description ?: ""
            )
        }
    }

    private fun onSaveAccountClick(account: SocialMediaAccount) {
        Log.e("cleanmimari", "onEditAccountClick triggered")
        viewModelScope.launch {
            val json = jsonConvertUseCase.socialMediaAccountModelToJson(account)
            _navigateToEditScreen.emit(json) // StateFlow ya da SharedFlow kullan

        }
    }

    fun processNavigationResult(json: String?) {
        if (!json.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    // UseCase katmanına veri dönüşümü işini devrediyoruz
                    val account = jsonConvertUseCase.jsonToSocialMediaAccountModel(json)
                    loadFromAccount(account)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun addAccount() {
        if (_uiState.value.platform.isPhone) {
            val (code, iso, nationalNumber) = parsePhoneNumberUseCase("${_countries.value.phoneCode}${_uiState.value.username}")
            _uiState.update { it.copy(username = "${code}${iso}${nationalNumber}") }
        }


        val updatedAccount = _currentAccount.value?.copy(
            platform = _uiState.value.platform,
            username = _uiState.value.username,
            description = _uiState.value.description
        ) ?: SocialMediaAccount(
            userId = Constants.DEFAULT_USER_ID,
            platform = _uiState.value.platform,
            username = _uiState.value.username,
            description = _uiState.value.description
        )
        _currentAccount.value = updatedAccount
        Log.e("telefondeneme", updatedAccount.toString())
        onSaveAccountClick(updatedAccount)

    }
}