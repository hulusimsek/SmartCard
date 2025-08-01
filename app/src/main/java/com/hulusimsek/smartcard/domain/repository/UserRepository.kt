package com.hulusimsek.smartcard.domain.repository

import com.hulusimsek.smartcard.data.local.entity.UserEntity
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // User işlemleri
    suspend fun insertUser(user: User): Long
    suspend fun updateUser(user: User): Int
    suspend fun deleteUser(user: User): Int
    suspend fun getUserById(id: Int): User?
    fun getUserStream(id: Int): Flow<User?>
    fun getAllUserStream(): Flow<List<User>>
}