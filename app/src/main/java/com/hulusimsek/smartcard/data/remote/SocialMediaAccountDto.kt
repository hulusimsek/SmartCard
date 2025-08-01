package com.hulusimsek.smartcard.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SocialMediaAccountDto(
    val id: Int = 0,
    val tempId: String,
    val userId: Int,
    val platform: String,
    val username: String,
    val description: String?,
    val isActive: Boolean
)