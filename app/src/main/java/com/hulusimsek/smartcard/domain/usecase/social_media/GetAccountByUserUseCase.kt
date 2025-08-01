package com.hulusimsek.smartcard.domain.usecase.social_media

import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.repository.SocialMediaRepository
import javax.inject.Inject

class GetAccountByUserUseCase @Inject constructor(
    private val repository: SocialMediaRepository
) {
    suspend operator fun invoke(userId: Int): List<SocialMediaAccount> {
        return repository.getAccountsByUser(userId)
    }
}