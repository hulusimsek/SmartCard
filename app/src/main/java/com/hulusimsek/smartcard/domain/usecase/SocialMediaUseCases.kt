package com.hulusimsek.smartcard.domain.usecase

import com.hulusimsek.smartcard.domain.usecase.social_media.DeleteAccountUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.GetAccountByUserUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.GetAccountStreamUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.InsertAccountUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.OpenSocialPlatformUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.ToggleAccountActiveStatusUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.UpdateAccountUseCase


data class SocialMediaUseCases(
    val insertAccount: InsertAccountUseCase,
    val updateAccount: UpdateAccountUseCase,
    val toggleAccountActive: ToggleAccountActiveStatusUseCase,
    val deleteAccount: DeleteAccountUseCase,
    val getAccountsByUser: GetAccountByUserUseCase,
    val openSocialPlatformUseCase: OpenSocialPlatformUseCase,
    val getAccountStream: GetAccountStreamUseCase
)