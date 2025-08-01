package com.hulusimsek.smartcard.domain.usecase.composite

import com.google.gson.Gson
import com.hulusimsek.smartcard.data.mapper.toDomain
import com.hulusimsek.smartcard.data.mapper.toDto
import com.hulusimsek.smartcard.data.mapper.toExportedUserData
import com.hulusimsek.smartcard.data.model.ExportedAccount
import com.hulusimsek.smartcard.data.model.ExportedUserData
import com.hulusimsek.smartcard.data.remote.SocialMediaAccountDto
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class JsonConverterUseCase @Inject constructor(

) {
    suspend fun jsonToSocialMediaAccountModel(json: String): SocialMediaAccount {
        // DTO to Domain mapping işlemi domain veya data katmanına taşındı
        val dto = Json.decodeFromString<SocialMediaAccountDto>(json)
        return dto.toDomain()
    }

    suspend fun socialMediaAccountModelToJson(model: SocialMediaAccount): String {
        // DTO to Domain mapping işlemi domain veya data katmanına taşındı
        val dto = model.toDto()
        val json = Json.encodeToString(dto)
        return json
    }

    fun convertUserProfileToJsonUseCase(
        user: User,
        activeAccounts: List<SocialMediaAccount>
    ): String {
        val exportedData = user.toExportedUserData(activeAccounts)

        return Json.encodeToString(exportedData)
    }

    fun convertJsonToUserProfileUseCase(
        json: String
    ): Pair<User, List<SocialMediaAccount>>? {
        return try {
            val exportedUserData = Json.decodeFromString<ExportedUserData>(json)
            val exportedData = exportedUserData.toDomain()
            exportedData
        } catch (e: Exception) {
            null
        }
    }

}