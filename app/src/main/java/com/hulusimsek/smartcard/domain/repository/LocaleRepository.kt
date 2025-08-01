package com.hulusimsek.smartcard.domain.repository

interface LocaleRepository {
    fun getSupportedLanguages(): List<String>
    fun getDeviceLanguage(): String
    fun getDeviceCountry(): String
}