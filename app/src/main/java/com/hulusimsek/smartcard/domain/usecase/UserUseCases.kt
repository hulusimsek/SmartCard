package com.hulusimsek.smartcard.domain.usecase

import com.hulusimsek.smartcard.domain.usecase.user.DeleteUserUseCase
import com.hulusimsek.smartcard.domain.usecase.user.GetAllUserStreamUseCase
import com.hulusimsek.smartcard.domain.usecase.user.GetUserByIdUseCase
import com.hulusimsek.smartcard.domain.usecase.user.GetUserStreamUseCase
import com.hulusimsek.smartcard.domain.usecase.user.InsertUserUseCase
import com.hulusimsek.smartcard.domain.usecase.user.UpdateUserUseCase

data class UserUseCases(
    val insertUser: InsertUserUseCase,
    val updateUser: UpdateUserUseCase,
    val deleteUser: DeleteUserUseCase,
    val getUserById: GetUserByIdUseCase,
    val getAllUserStream: GetAllUserStreamUseCase,
    val getUserStream: GetUserStreamUseCase
)