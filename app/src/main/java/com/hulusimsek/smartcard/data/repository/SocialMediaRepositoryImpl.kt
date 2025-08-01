package com.hulusimsek.smartcard.data.repository

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.hulusimsek.smartcard.data.local.dao.SocialMediaAccountDao
import com.hulusimsek.smartcard.data.mapper.toDomain
import com.hulusimsek.smartcard.data.mapper.toEntity
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.domain.repository.SocialMediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.core.net.toUri
import com.hulusimsek.smartcard.domain.repository.IntentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SocialMediaRepositoryImpl(
    private val intentRepository: IntentRepository,
    private val accountDao: SocialMediaAccountDao
) : SocialMediaRepository {

    // Social account işlemleri
    override suspend fun insertAccount(account: SocialMediaAccount): Long {
        return accountDao.insertAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: SocialMediaAccount): Int {
        return accountDao.updateAccount(account.toEntity())
    }

    override suspend fun toggleAccountActiveStatus(accountId: Int, currentStatus: Boolean) {
        accountDao.updateAccountActiveStatus(accountId, currentStatus)
    }

    override suspend fun deleteAccount(account: SocialMediaAccount): Int {
        return accountDao.deleteAccount(account.toEntity())
    }

    override suspend fun getAccountsByUser(userId: Int): List<SocialMediaAccount> {
        return accountDao.getAccountsByUser(userId).map { it.toDomain() }
    }

    override fun getAccountStream(userId: Int): Flow<List<SocialMediaAccount>> {
        return accountDao.getAccountStream(userId).map { it.map { it.toDomain() } }
    }

    override suspend fun openSocialPlatform(
        platform: SocialPlatform,
        username: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val success = when (platform) {
                    SocialPlatform.PHONE -> openPhoneCall(username)
                    SocialPlatform.EMAIL -> openEmail(username)
                    SocialPlatform.WHATSAPP -> openWhatsApp(username)
                    SocialPlatform.WEBSITE -> openWebsite(username)
                    else -> openSocialApp(platform, username)
                }

                if (success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("${platform.displayName} açılamadı - uygun uygulama bulunamadı"))
                }
            } catch (e: Exception) {
                Log.e(
                    "SocialMediaRepository",
                    "Platform açılırken hata: ${platform.displayName}",
                    e
                )
                Result.failure(e)
            }
        }
    }

    private suspend fun openSocialApp(platform: SocialPlatform, username: String): Boolean {
        val cleanUsername = username.trim().removePrefix("@")

        // Validasyon
        if (cleanUsername.isBlank()) {
            throw IllegalArgumentException("Kullanıcı adı boş olamaz")
        }

        Log.d(
            "SocialMedia",
            "Açılmaya çalışılan platform: ${platform.displayName}, username: $cleanUsername"
        )

        // Önce web URL'i dene - daha güvenilir
        val webUrl = "${platform.baseUrl}$cleanUsername"
        Log.d("SocialMedia", "Web URL deneniyor: $webUrl")

        val webResult = intentRepository.openIntent(webUrl)
        Log.d("SocialMedia", "Web URL sonucu: $webResult")

        if (webResult) {
            Log.d("SocialMedia", "Web URL başarılı: $webUrl")
            return true
        }

        // Web başarısızsa app'i dene
        platform.packageName?.let { packageName ->
            Log.d("SocialMedia", "App kontrolü: $packageName")
            val isAppInstalled = intentRepository.isAppInstalled(packageName)
            Log.d("SocialMedia", "App yüklü mü: $isAppInstalled")

            if (isAppInstalled) {
                platform.appScheme?.let { scheme ->
                    val appUri = "$scheme$cleanUsername"
                    Log.d("SocialMedia", "App URI deneniyor: $appUri")

                    val appResult = intentRepository.openIntent(appUri, packageName)
                    Log.d("SocialMedia", "App URI sonucu: $appResult")

                    if (appResult) {
                        Log.d("SocialMedia", "App URI başarılı: $appUri")
                        return true
                    } else {
                        Log.w("SocialMedia", "App URI başarısız: $appUri")
                    }
                }
            }
        }

        Log.e("SocialMedia", "Tüm deneme yöntemleri başarısız")
        return false
    }

    private suspend fun openPhoneCall(phoneNumber: String): Boolean {
        val cleanNumber = phoneNumber.replace(Regex("[^+\\d]"), "")

        if (cleanNumber.isBlank()) {
            throw IllegalArgumentException("Geçersiz telefon numarası")
        }

        return intentRepository.openIntent("tel:$cleanNumber", action = Intent.ACTION_DIAL)
    }

    private suspend fun openEmail(email: String): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw IllegalArgumentException("Geçersiz e-posta adresi")
        }

        return intentRepository.openIntent("mailto:$email", action = Intent.ACTION_SENDTO)
    }

    private suspend fun openWhatsApp(phoneNumber: String): Boolean {
        val cleanNumber = phoneNumber.replace(Regex("[^+\\d]"), "")

        if (cleanNumber.isBlank()) {
            throw IllegalArgumentException("Geçersiz telefon numarası")
        }

        // WhatsApp app varsa app'i dene
        if (intentRepository.isAppInstalled(SocialPlatform.WHATSAPP.packageName!!)) {
            if (intentRepository.openIntent(
                    "${SocialPlatform.WHATSAPP.baseUrl}$cleanNumber",
                    SocialPlatform.WHATSAPP.packageName
                )
            ) {
                return true
            }
        }

        // Web fallback
        return intentRepository.openIntent("${SocialPlatform.WHATSAPP.baseUrl}$cleanNumber")
    }

    private suspend fun openWebsite(url: String): Boolean {
        val formattedUrl = formatUrl(url)
        return intentRepository.openIntent(formattedUrl)
    }

    private fun formatUrl(url: String): String {
        return when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("www.") -> "https://$url"
            url.contains(".") -> "https://$url"
            else -> "https://www.$url.com"
        }
    }
}