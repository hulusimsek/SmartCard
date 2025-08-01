package com.hulusimsek.smartcard.data.repository

import android.content.Context
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.domain.repository.LocaleRepository
import java.util.Locale
import javax.inject.Inject

class LocaleRepositoryImp @Inject constructor(
    private val context: Context
) : LocaleRepository {

    override fun getSupportedLanguages(): List<String> {
        return context.resources.getStringArray(R.array.supported_languages).toList()
    }


    override fun getDeviceLanguage(): String {
        return Locale.getDefault().language
    }

    override fun getDeviceCountry(): String {
        return Locale.getDefault().country
    }
}