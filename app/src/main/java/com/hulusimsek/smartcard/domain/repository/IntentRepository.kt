package com.hulusimsek.smartcard.domain.repository

interface IntentRepository {
    suspend fun openIntent(uri: String, packageName: String? = null, action: String? = null): Boolean
    suspend fun isAppInstalled(packageName: String): Boolean
}