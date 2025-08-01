package com.hulusimsek.smartcard.domain.usecase.user

import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.repository.UserRepository
import javax.inject.Inject

class InsertUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User): Long {
        return repository.insertUser(user)
    }
}