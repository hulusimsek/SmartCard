package com.hulusimsek.smartcard.presentation.edit

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.usecase.SocialMediaUseCases
import com.hulusimsek.smartcard.domain.usecase.UserUseCases
import com.hulusimsek.smartcard.domain.usecase.composite.UpdateUserAndAccountsUseCase
import com.hulusimsek.smartcard.core.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import com.hulusimsek.smartcard.domain.usecase.StorageUseCases
import com.hulusimsek.smartcard.domain.usecase.composite.JsonConverterUseCase
import com.hulusimsek.smartcard.domain.usecase.composite.ParsePhoneNumberUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

@HiltViewModel
class EditViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val socialMediaUseCases: SocialMediaUseCases,
    private val updateUserAndAccountsUseCase: UpdateUserAndAccountsUseCase,
    private val parsePhoneNumberUseCase: ParsePhoneNumberUseCase,
    private val jsonConvertUseCase: JsonConverterUseCase,
    private val storageUseCases: StorageUseCases
) : ViewModel() {


    private val _tempUserInfo = MutableStateFlow<EditableUserInfo?>(null)
    val tempUserInfo: StateFlow<EditableUserInfo?> = _tempUserInfo

    // Kullanıcı bilgileri için StateFlow
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _socialAccounts = MutableStateFlow<List<SocialMediaAccount>?>(null)
    val socialAccounts: StateFlow<List<SocialMediaAccount>?> = _socialAccounts

    private val _navigateToAccountScreen = MutableSharedFlow<String>()
    val navigateToAccountScreen = _navigateToAccountScreen.asSharedFlow()

    private var isTempDataInitialized by mutableStateOf(false)

    var tempImageUri: Uri? = null

    init {
        Log.e("veriAktarımı", "viewmodel oluşturuldu")

        loadUser()
        loadSocialAccounts()
    }

    private fun loadSocialAccounts() {
        viewModelScope.launch {
            _socialAccounts.value = socialMediaUseCases.getAccountsByUser(Constants.DEFAULT_USER_ID)
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = userUseCases.getUserById(Constants.DEFAULT_USER_ID)
        }
    }

    fun initializeTempData(user: User?, accounts: List<SocialMediaAccount>) {
        if (isTempDataInitialized) return
        Log.e("veriAktarımı", "initializeTempData çalıştırıldı")
        if (user != null) {
            _tempUserInfo.value = EditableUserInfo(
                firstName = user.firstName.orEmpty(),
                lastName = user.lastName.orEmpty(),
                profileImageUri = user.profileImagePath?.toUri(),
                socialAccounts = accounts
            )
            isTempDataInitialized = true
        }
    }

    fun updateTempFirstName(name: String) {
        _tempUserInfo.update { it?.copy(firstName = name) }
    }

    fun updateTempLastName(name: String) {
        _tempUserInfo.update { it?.copy(lastName = name) }
    }

    fun toggleTempSocialAccount(account: SocialMediaAccount) {
        val updatedAccounts = _tempUserInfo.value?.socialAccounts?.map {
            if (it.id == account.id) it.copy(isActive = !it.isActive) else it
        }
        updatedAccounts?.let {updatedAccountsList -> _tempUserInfo.update { it?.copy(socialAccounts = updatedAccountsList) } }
    }

    private fun updateTempSocialAccount(updatedAccount: SocialMediaAccount) {

        /*
        tempUserInfo = tempUserInfo?.copy(socialAccounts = tempUserInfo?.socialAccounts?.map { account ->
            if (account.id != 0 && updatedAccount.id != 0 && account.id == updatedAccount.id) {
                updatedAccount
            } else if (account.id == 0 && updatedAccount.id == 0 && account.tempId == updatedAccount.tempId) {
                updatedAccount
            } else {
                account
            }
            } ?: listOf()
        )
         */

        val currentAccounts = _tempUserInfo.value?.socialAccounts?.map { it.copy() } ?: emptyList()

        val updatedList = currentAccounts.toMutableList()

        // 1. Güncellenecek hesabı bul
        val index = currentAccounts.indexOfFirst { existing ->
            // Gerçek id ile karşılaştır (veritabanından gelen)                 // Geçici id ile karşılaştır (eklenmiş ama db'ye gitmemiş)
            (updatedAccount.id != 0 && existing.id == updatedAccount.id) || (updatedAccount.id == 0 && existing.tempId == updatedAccount.tempId)
        }



        if (index != -1) {
            // 2. Güncelle
            updatedList[index] = updatedAccount
        } else {
            // 3. Yeni hesap → listeye ekle
            updatedList.add(updatedAccount)
        }
        Log.e("veriAktarımı", "updateTempSocialAccount $updatedList")
        // 4. State'i güncelle
        _tempUserInfo.update { user ->
            user?.copy(socialAccounts = updatedList.map { it.copy() })
        }
        Log.e("veriAktarımı", "updateTempSocialAccount tempUserInfo ${_tempUserInfo.value?.socialAccounts}")
    }

    fun deleteTempSocialAccount(account: SocialMediaAccount) {
        _tempUserInfo.value?.let { user ->
            _tempUserInfo.update {
                user.copy(
                    socialAccounts = user.socialAccounts.filter { it.id != account.id }
                )
            }
        }
    }

    fun onEditAccountClick(account: SocialMediaAccount?) {
        Log.e("cleanmimari", "onEditAccountClick triggered")
        viewModelScope.launch {
            if(account == null) {
                _navigateToAccountScreen.emit("")
                return@launch
            }
            val json = jsonConvertUseCase.socialMediaAccountModelToJson(account)
            _navigateToAccountScreen.emit(json) // StateFlow ya da SharedFlow kullan

        }
    }

    fun processNavigationResult(json: String?) {
        if (!json.isNullOrEmpty()) {
            viewModelScope.launch {
                try {
                    // UseCase katmanına veri dönüşümü işini devrediyoruz
                    val account = jsonConvertUseCase.jsonToSocialMediaAccountModel(json)

                    // Geçici hesabı güncelle
                    updateTempSocialAccount(account)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun persistChanges() {
        viewModelScope.launch {
            val temp = _tempUserInfo.first()?.socialAccounts
            Log.e("veriAktarımı", "persistChanges temp: $temp")

            val oldImagePath = user.value?.profileImagePath

            var newImagePath: String? = null

             if(_tempUserInfo.value?.profileImageUri == null) {
                 newImagePath = oldImagePath
            } else {
                 newImagePath = storageUseCases.saveProfileImage(_tempUserInfo.value?.profileImageUri!!, oldImagePath)
            }

            val updatedUser = user.value?.copy(
                firstName = _tempUserInfo.value?.firstName ?: "",
                lastName = _tempUserInfo.value?.lastName ?: "",
                profileImagePath = newImagePath
            )

            if (updatedUser != null) {
                Log.e("veriAktarımı", "persistChanges " + _tempUserInfo.value?.socialAccounts.toString())
                updateUserAndAccountsUseCase(
                    updatedUser = updatedUser,
                    originalAccounts = socialAccounts.value ?: listOf(),
                    updatedAccounts = tempUserInfo.value?.socialAccounts ?: listOf()
                )

                if (!oldImagePath.isNullOrEmpty()  && oldImagePath != newImagePath) {
                    storageUseCases.deleteProfileImage(oldImagePath)
                }
            }
        }
    }

    fun createTempImageUri(context: Context): Uri? {
        return storageUseCases.createTempImageUri(context)
    }

    fun updateTempProfilImage(uri: Uri) {
        _tempUserInfo.update { it?.copy(profileImageUri = uri) }
    }

    fun clearTempProfileImages(context: Context) {
        storageUseCases.clearTempProfileImages(context)
    }
}
