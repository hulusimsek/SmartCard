package com.hulusimsek.smartcard.data.mapper

import com.hulusimsek.smartcard.data.local.entity.SocialMediaAccountEntity
import com.hulusimsek.smartcard.data.model.ExportedAccount
import com.hulusimsek.smartcard.data.remote.SocialMediaAccountDto
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.SocialPlatform

fun SocialMediaAccountEntity.toDomain(): SocialMediaAccount {
    return SocialMediaAccount(
        id = id,
        userId = userId,
        platform = SocialPlatform.valueOf(platform),
        username = username,
        description = description,
        isActive = isActive
    )
}

fun SocialMediaAccount.toEntity(): SocialMediaAccountEntity {
    return SocialMediaAccountEntity(
        id = id,
        userId = userId,
        platform = platform.name,
        username = username,
        description = description,
        isActive = isActive
    )
}

fun SocialMediaAccountDto.toDomain() = SocialMediaAccount(
    id = id,
    userId = userId,
    tempId = tempId,
    platform = SocialPlatform.valueOf(platform),
    username = username,
    description = description,
    isActive = isActive
)

fun SocialMediaAccount.toDto() = SocialMediaAccountDto(
    id = id,
    userId = userId,
    tempId = tempId,
    platform = platform.name,
    username = username,
    description = description,
    isActive = isActive
)


fun SocialMediaAccount.toExportedAccount(): ExportedAccount {
    return ExportedAccount(
        platform = this.platform.name,
        username = this.username,
        description = this.description
    )
}

fun ExportedAccount.toDto(): SocialMediaAccount {
    return SocialMediaAccount(
        id = 0,
        userId = 0,
        username = username,
        platform = SocialPlatform.valueOf(platform),
        description = description,
    )
}