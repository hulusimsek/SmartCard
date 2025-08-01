package com.hulusimsek.smartcard.domain.usecase.social_media

import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.repository.SocialMediaRepository
import com.hulusimsek.smartcard.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountStreamUseCase @Inject constructor(
    private val repository: SocialMediaRepository
) {
    operator fun invoke(userId: Int): Flow<List<SocialMediaAccount>> {
        return repository.getAccountStream(userId)
    }
}