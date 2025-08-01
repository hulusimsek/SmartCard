package com.hulusimsek.smartcard.data.mapper

import com.hulusimsek.smartcard.data.local.entity.UserEntity
import com.hulusimsek.smartcard.data.model.ExportedUserData
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        createdAt = createdAt,
        profileImagePath = profileImagePath
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        createdAt = createdAt,
        profileImagePath = profileImagePath
    )
}

fun User.toExportedUserData(activeAccounts: List<SocialMediaAccount>): ExportedUserData {
    return ExportedUserData(
        firstName = this.firstName,
        lastName = this.lastName,
        accounts = activeAccounts.map { it.toExportedAccount() }
    )
}

fun ExportedUserData.toDomain(): Pair<User, List<SocialMediaAccount>> {
    val user = User(
        firstName = firstName,
        lastName = lastName,
        createdAt = null,
        profileImagePath = null // dışarıdan gelen JSON'da olmayabilir
    )

    val accounts = accounts.map {
        it.toDto().copy(userId = 0)
    }

    return user to accounts
}