package com.hulusimsek.smartcard.data.repository

import android.util.Log
import com.hulusimsek.smartcard.data.local.dao.UserDao
import com.hulusimsek.smartcard.data.local.dao.SocialMediaAccountDao
import com.hulusimsek.smartcard.data.local.entity.UserEntity
import com.hulusimsek.smartcard.data.mapper.toDomain
import com.hulusimsek.smartcard.data.mapper.toEntity
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao,
) : UserRepository {

    // User işlemleri
    override suspend fun insertUser(user: User) : Long {
        return userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) : Int {
        return userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(user: User): Int {
        return userDao.deleteUser(user.toEntity())
    }

    override suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)?.toDomain()
    }

    override fun getUserStream(id: Int): Flow<User?> {
        return userDao.getUserStream(id).map { it?.toDomain() }
    }

    override fun getAllUserStream(): Flow<List<User>> {
        return userDao.getAllUserStream().map { userEntityList ->
            userEntityList.map { it.toDomain() }
        }
    }
}