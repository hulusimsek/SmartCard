package com.hulusimsek.smartcard.domain.model

data class User(
    val id: Int = 0,
    val firstName: String?,
    val lastName: String?,
    val createdAt: String?,
    val profileImagePath: String? // Dosya yolu olabilir
)