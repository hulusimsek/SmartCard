package com.hulusimsek.smartcard.presentation.friend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.core.util.ResourceProvider
import com.hulusimsek.smartcard.core.util.TimeUtils
import com.hulusimsek.smartcard.domain.model.NfcData
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.usecase.SocialMediaUseCases
import com.hulusimsek.smartcard.domain.usecase.UserUseCases
import com.hulusimsek.smartcard.domain.usecase.composite.JsonConverterUseCase
import com.hulusimsek.smartcard.presentation.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NfcDetailViewModel @Inject constructor(
    private val jsonConvertUseCase: JsonConverterUseCase,
    private val userUseCases: UserUseCases,
    private val resourceProvider: ResourceProvider,
    private val socialMediaUseCases: SocialMediaUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendUiState())
    val uiState: StateFlow<FriendUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SocialMediaUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    fun setReceivedData(json: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val pairData = jsonConvertUseCase.convertJsonToUserProfileUseCase(json)
            pairData?.let { pair ->
                pair.first.let { newUser ->
                    _uiState.update {
                        it.copy(
                            user = newUser,
                            socialAccounts = pair.second,
                            isFromDB = false,
                            isLoading = false
                        )
                    }
                }

                Log.e("friendScreenDeneme", _uiState.value.toString())
                return
            }
            _uiState.update {
                it.copy(
                    errorMessage = resourceProvider.getString(R.string.error_user_data_not_processed),
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = resourceProvider.getString(R.string.error_data_read, e.message ?: ""),
                    isLoading = false
                )
            }
        }
    }

    fun setData(id: String) {
        try {
            _uiState.update { it.copy(isLoading = true) }
            val userId = id.toIntOrNull()
            viewModelScope.launch {
                userId?.let { intID->
                    _uiState.update { state ->
                        state.copy(
                            user = userUseCases.getUserById(intID),
                            socialAccounts = socialMediaUseCases.getAccountsByUser(intID),
                            isFromDB = true,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = resourceProvider.getString(R.string.error_occurred)) }

        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun deleteUser(user: User?, onBack: () -> Unit) {
        viewModelScope.launch {
            user?.let { selectedUser ->
                _uiState.update { it.copy(isLoading = true) }
                userUseCases.deleteUser(selectedUser)
                _uiState.update { it.copy(isLoading = false) }
                onBack()
            }
        }
    }

    fun saveNewProfile(onBack: () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.user == null) {
                sendErrorEvent(resourceProvider.getString(R.string.error_user_info_not_found))
                return@launch
            }
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // 1. Kullanıcıyı ekle ve id'yi al
                val newUserId =
                    userUseCases.insertUser(_uiState.value.user!!.copy(createdAt = TimeUtils.getCurrentDateTimeString()))

                // 2. Her hesabın userId'sini bu yeni id ile güncelle
                val socialAccountsWithUserId = _uiState.value.socialAccounts.map { account ->
                    account.copy(userId = newUserId.toInt())
                }

                // 3. Her bir sosyal medya hesabını veritabanına ekle
                socialAccountsWithUserId.forEach { account ->
                    socialMediaUseCases.insertAccount(account)
                }

                onBack()

            } catch (e: Exception) {
                // Hata yönetimi
                sendErrorEvent(resourceProvider.getString(R.string.error_occurred))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }

    }

    fun onSocialMediaClicked(platform: SocialPlatform, username: String) {
        if (username.isBlank()) {
            sendErrorEvent(resourceProvider.getString(R.string.error_username_empty))
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            socialMediaUseCases.openSocialPlatformUseCase(platform, username)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    Log.d("SocialMedia", "${platform.displayName} başarıyla açıldı")
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false)

                    val errorMessage = when (exception) {
                        is IllegalArgumentException -> exception.message ?: resourceProvider.getString(R.string.error_invalid_parameter)
                        else -> resourceProvider.getString(R.string.error_cannot_open_platform, exception, platform.displayName, exception.message ?: "")
                    }

                    Log.e(
                        "SocialMedia",
                        "Platform açılırken hata: ${platform.displayName}",
                        exception
                    )
                    sendErrorEvent(errorMessage)
                }
        }
    }


    private fun sendErrorEvent(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(SocialMediaUiEvent.ShowError(message))
        }
    }

}