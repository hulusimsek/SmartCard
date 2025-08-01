package com.hulusimsek.smartcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Tek kullanıcı olduğu için sabit
    val firstName: String?,
    val lastName: String?,
    val createdAt: String?,
    val profileImagePath: String?
)