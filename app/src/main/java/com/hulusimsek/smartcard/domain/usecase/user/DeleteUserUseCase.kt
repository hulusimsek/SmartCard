package com.hulusimsek.smartcard.domain.usecase.user

import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteUserUseCase  @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User): Int {
        return repository.deleteUser(user)
    }
}
