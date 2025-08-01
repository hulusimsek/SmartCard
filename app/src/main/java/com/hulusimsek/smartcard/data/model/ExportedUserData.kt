package com.hulusimsek.smartcard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ExportedUserData(
    val firstName: String?,
    val lastName: String?,
    val accounts: List<ExportedAccount>
)