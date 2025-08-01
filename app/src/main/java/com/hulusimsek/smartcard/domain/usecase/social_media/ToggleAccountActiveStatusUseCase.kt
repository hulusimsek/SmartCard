package com.hulusimsek.smartcard.domain.usecase.social_media

import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.repository.SocialMediaRepository

class ToggleAccountActiveStatusUseCase(
    private val repository: SocialMediaRepository
) {
    suspend operator fun invoke(account: SocialMediaAccount) {
        repository.toggleAccountActiveStatus(account.id, !account.isActive)
    }
}