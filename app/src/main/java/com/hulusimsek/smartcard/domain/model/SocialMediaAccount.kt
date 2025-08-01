package com.hulusimsek.smartcard.domain.model

import java.util.UUID

data class SocialMediaAccount(
    val id: Int = 0,
    val userId: Int, // Foreign key
    val tempId: String = UUID.randomUUID().toString(),
    val platform: SocialPlatform,
    val username: String,
    val description: String? = null,
    val isActive: Boolean = true
)