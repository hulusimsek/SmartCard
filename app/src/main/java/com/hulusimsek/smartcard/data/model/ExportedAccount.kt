package com.hulusimsek.smartcard.data.model

import kotlinx.serialization.Serializable


@Serializable
data class ExportedAccount(
    val platform: String,
    val username: String,
    val description: String?
)