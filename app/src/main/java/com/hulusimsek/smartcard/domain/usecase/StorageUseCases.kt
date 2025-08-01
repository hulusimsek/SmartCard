package com.hulusimsek.smartcard.domain.usecase

import com.hulusimsek.smartcard.domain.usecase.storage.ClearTempProfileImagesUseCase
import com.hulusimsek.smartcard.domain.usecase.storage.CreateTempImageUriUseCase
import com.hulusimsek.smartcard.domain.usecase.storage.DeleteProfileImageUseCase
import com.hulusimsek.smartcard.domain.usecase.storage.SaveProfileImageUseCase

data class StorageUseCases(
    val createTempImageUri: CreateTempImageUriUseCase,
    val deleteProfileImage: DeleteProfileImageUseCase,
    val clearTempProfileImages: ClearTempProfileImagesUseCase,
    val saveProfileImage: SaveProfileImageUseCase,
)