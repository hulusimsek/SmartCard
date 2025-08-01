package com.hulusimsek.smartcard.presentation.social_media_account

import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.core.util.Constants

data class AccountUiState(
    val platform: SocialPlatform = SocialPlatform.INSTAGRAM,
    val username: String = "",
    val isPhoneValid: Boolean = false,
    val description: String = ""
) {
    val isSaveEnabled: Boolean
        get() = when (platform.isPhone) {
            true -> isPhoneValid
            else -> username.isNotBlank()
        }
}