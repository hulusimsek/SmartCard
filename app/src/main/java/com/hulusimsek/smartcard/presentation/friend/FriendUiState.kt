package com.hulusimsek.smartcard.presentation.friend

import com.hulusimsek.smartcard.domain.model.NfcMode
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User

data class FriendUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val socialAccounts: List<SocialMediaAccount> = emptyList(),
    val isFromDB: Boolean = false,
    val errorMessage: String? = null,
)