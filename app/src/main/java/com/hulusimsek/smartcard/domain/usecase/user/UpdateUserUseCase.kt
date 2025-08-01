package com.hulusimsek.smartcard.domain.usecase.user

import android.util.Log
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User): Int {
        return repository.updateUser(user)
    }
}