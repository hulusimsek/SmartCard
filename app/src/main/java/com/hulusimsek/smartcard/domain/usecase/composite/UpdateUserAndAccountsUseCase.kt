package com.hulusimsek.smartcard.domain.usecase.composite

import android.util.Log
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.usecase.SocialMediaUseCases
import com.hulusimsek.smartcard.domain.usecase.UserUseCases
import javax.inject.Inject

class UpdateUserAndAccountsUseCase @Inject constructor(
    private val userUseCases: UserUseCases,
    private val socialMediaUseCases: SocialMediaUseCases
) {
    suspend operator fun invoke(
        updatedUser: User,
        originalAccounts: List<SocialMediaAccount>,
        updatedAccounts: List<SocialMediaAccount>
    ) {
        Log.e("veriAktarımı", "originalAccounts " + originalAccounts.toString())
        Log.e("veriAktarımı", "updatedAccounts " + updatedAccounts.toString())
        userUseCases.updateUser(updatedUser)
        // Silinen hesaplar
        val deletedAccounts = originalAccounts.filter { original ->
            updatedAccounts.none { it.id == original.id }
        }
        deletedAccounts.forEach { socialMediaUseCases.deleteAccount(it) }

        // Yeni eklenen hesaplar
        val newAccounts = updatedAccounts.filter { updated ->
            updated.id == 0 || updated.id == null || originalAccounts.none { it.id == updated.id }
        }
        Log.e("veriAktarımı", "eklenen hesaplar " + newAccounts.toString())
        newAccounts.forEach { socialMediaUseCases.insertAccount(it) }


        // Güncellenen hesaplar
        val changedAccounts = updatedAccounts.filter { updated ->
            originalAccounts.any { it.id == updated.id && it != updated }
        }
        Log.e("veriAktarımı", "değişen hesaplar " + changedAccounts.toString())
        changedAccounts.forEach { socialMediaUseCases.updateAccount(it) }
    }
}