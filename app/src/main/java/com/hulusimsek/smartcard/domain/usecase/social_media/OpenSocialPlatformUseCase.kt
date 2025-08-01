package com.hulusimsek.smartcard.domain.usecase.social_media

import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.domain.repository.SocialMediaRepository
import javax.inject.Inject

class OpenSocialPlatformUseCase(
    private val repository: SocialMediaRepository
) {
    suspend operator fun invoke(platform: SocialPlatform, username: String): Result<Unit> {
        return try {
            repository.openSocialPlatform(platform, username)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}