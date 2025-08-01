package com.hulusimsek.smartcard.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "social_media_accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,         // Ana tablo
            parentColumns = ["id"],             // UserEntity’deki id
            childColumns = ["userId"],          // Bu tabloda userId
            onDelete = ForeignKey.CASCADE       // Kullanıcı silinince bu hesaplar da silinir
        )
    ]
)
data class SocialMediaAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Foreign key
    val platform: String,
    val username: String,
    val description: String?,
    val isActive: Boolean = true
)