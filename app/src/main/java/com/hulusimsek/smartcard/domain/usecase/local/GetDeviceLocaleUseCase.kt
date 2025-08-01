package com.hulusimsek.smartcard.domain.usecase.local

import com.hulusimsek.smartcard.domain.repository.LocaleRepository
import javax.inject.Inject

class GetDeviceLocaleUseCase  @Inject constructor(
    private val languageProvider: LocaleRepository
) {
    operator fun invoke(): String {
        return languageProvider.getDeviceCountry()
    }
}