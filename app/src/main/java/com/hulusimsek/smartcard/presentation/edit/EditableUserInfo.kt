package com.hulusimsek.smartcard.presentation.edit

import android.net.Uri
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount

data class EditableUserInfo(
    val firstName: String = "",
    val lastName: String = "",
    val profileImageUri: Uri? = null,
    val socialAccounts: List<SocialMediaAccount> = emptyList()
)