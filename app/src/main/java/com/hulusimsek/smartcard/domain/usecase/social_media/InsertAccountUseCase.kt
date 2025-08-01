package com.hulusimsek.smartcard.domain.usecase.social_media

import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.repository.SocialMediaRepository
import com.hulusimsek.smartcard.domain.repository.UserRepository
import javax.inject.Inject

class InsertAccountUseCase @Inject constructor(
    private val repository: SocialMediaRepository
) {
    suspend operator fun invoke(account: SocialMediaAccount): Long {
        return repository.insertAccount(account)
    }
}