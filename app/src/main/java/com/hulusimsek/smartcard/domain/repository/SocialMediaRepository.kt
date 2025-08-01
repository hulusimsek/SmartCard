package com.hulusimsek.smartcard.domain.repository

import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SocialMediaRepository {

    // Sosyal medya hesapları
    suspend fun insertAccount(account: SocialMediaAccount): Long
    suspend fun updateAccount(account: SocialMediaAccount): Int
    suspend fun toggleAccountActiveStatus(accountId: Int, currentStatus: Boolean)
    suspend fun deleteAccount(account: SocialMediaAccount): Int
    suspend fun getAccountsByUser(userId: Int): List<SocialMediaAccount>
    fun getAccountStream(userId: Int): Flow<List<SocialMediaAccount>>

    suspend fun openSocialPlatform(platform: SocialPlatform, username: String): Result<Unit>
}