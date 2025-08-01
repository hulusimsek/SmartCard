package com.hulusimsek.smartcard.domain.usecase.user

import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserStreamUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(id: Int): Flow<User?> {
        return repository.getUserStream(id)
    }
}