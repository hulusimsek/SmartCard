package com.hulusimsek.smartcard.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.hulusimsek.smartcard.core.util.ResourceProvider
import com.hulusimsek.smartcard.data.local.AppDatabase
import com.hulusimsek.smartcard.data.local.dao.SocialMediaAccountDao
import com.hulusimsek.smartcard.data.local.dao.UserDao
import com.hulusimsek.smartcard.data.provider.ActivityProviderImpl
import com.hulusimsek.smartcard.data.repository.IntentRepositoryImpl
import com.hulusimsek.smartcard.data.repository.LocaleRepositoryImp
import com.hulusimsek.smartcard.data.repository.NfcRepositoryImpl
import com.hulusimsek.smartcard.data.repository.QrCodeRepositoryImpl
import com.hulusimsek.smartcard.data.repository.SocialMediaRepositoryImpl
import com.hulusimsek.smartcard.data.repository.UserRepositoryImpl
import com.hulusimsek.smartcard.domain.provider.ActivityProvider
import com.hulusimsek.smartcard.domain.repository.IntentRepository
import com.hulusimsek.smartcard.domain.repository.LocaleRepository
import com.hulusimsek.smartcard.domain.repository.NfcRepository
import com.hulusimsek.smartcard.domain.repository.QrCodeRepository
import com.hulusimsek.smartcard.domain.repository.SocialMediaRepository
import com.hulusimsek.smartcard.domain.repository.UserRepository
import com.hulusimsek.smartcard.domain.usecase.SocialMediaUseCases
import com.hulusimsek.smartcard.domain.usecase.StorageUseCases
import com.hulusimsek.smartcard.domain.usecase.UserUseCases
import com.hulusimsek.smartcard.domain.usecase.composite.JsonConverterUseCase
import com.hulusimsek.smartcard.domain.usecase.composite.ParsePhoneNumberUseCase
import com.hulusimsek.smartcard.domain.usecase.composite.UpdateUserAndAccountsUseCase
import com.hulusimsek.smartcard.domain.usecase.local.GetDeviceLocaleUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.DisableNfcAllUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.EnableNfcListenerUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.EnableNfcSenderUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.IsNfcAvailableUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.IsNfcEnabledUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.ObserveNfcDataUseCase
import com.hulusimsek.smartcard.domain.usecase.nfc.ResumeNfcAllUseCase
import com.hulusimsek.smartcard.domain.usecase.qrcode.GenerateQrCodeUse
import com.hulusimsek.smartcard.domain.usecase.social_media.DeleteAccountUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.GetAccountByUserUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.GetAccountStreamUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.InsertAccountUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.OpenSocialPlatformUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.ToggleAccountActiveStatusUseCase
import com.hulusimsek.smartcard.domain.usecase.social_media.UpdateAccountUseCase
import com.hulusimsek.smartcard.domain.usecase.storage.ClearTempProfileImagesUseCase
import com.hulusimsek.smartcard.domain.usecase.storage.CreateTempImageUriUseCase
import com.hulusimsek.smartcard.domain.usecase.storage.DeleteProfileImageUseCase
import com.hulusimsek.smartcard.domain.usecase.storage.SaveProfileImageUseCase
import com.hulusimsek.smartcard.domain.usecase.user.DeleteUserUseCase
import com.hulusimsek.smartcard.domain.usecase.user.GetAllUserStreamUseCase
import com.hulusimsek.smartcard.domain.usecase.user.GetUserByIdUseCase
import com.hulusimsek.smartcard.domain.usecase.user.GetUserStreamUseCase
import com.hulusimsek.smartcard.domain.usecase.user.InsertUserUseCase
import com.hulusimsek.smartcard.domain.usecase.user.UpdateUserUseCase
import com.hulusimsek.smartcard.presentation.common.ResourceProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun provideSocialMediaAccountDao(database: AppDatabase): SocialMediaAccountDao {
        return database.socialMediaAccountDao()
    }

    // Repository
    @Singleton
    @Provides
    fun provideUserRepository(
        userDao: UserDao,
    ) = UserRepositoryImpl(userDao) as UserRepository


    @Singleton
    @Provides
    fun provideSocialMediaRepository(
        intentRepository: IntentRepository,
        accountDao: SocialMediaAccountDao
    ) = SocialMediaRepositoryImpl(intentRepository, accountDao) as SocialMediaRepository


    @Provides
    @Singleton
    fun provideLocaleRepository(@ApplicationContext context: Context): LocaleRepository {
        return LocaleRepositoryImp(context)
    }

    @Provides
    @Singleton
    fun provideQrCodeRepository(): QrCodeRepository {
        return QrCodeRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideActivityProvider(
    ): ActivityProvider {
        return ActivityProviderImpl()
    }


    @Provides
    @Singleton
    fun provideNfcRepository(
        @ApplicationContext context: Context,
        activityProvider: ActivityProvider
    ): NfcRepository {
        return NfcRepositoryImpl(context, activityProvider)
    }

    @Provides
    @Singleton
    fun provideIntentRepository(
        @ApplicationContext context: Context
    ): IntentRepository {
        return IntentRepositoryImpl(context)
    }


    @Singleton
    @Provides
    fun provideEnableNfcListenerUseCase(
        nfcRepository: NfcRepository
    ): EnableNfcListenerUseCase {
        return EnableNfcListenerUseCase(nfcRepository)
    }

    @Singleton
    @Provides
    fun provideEnableNfcSenderUseCase(
        nfcRepository: NfcRepository
    ): EnableNfcSenderUseCase {
        return EnableNfcSenderUseCase(nfcRepository)
    }

    @Singleton
    @Provides
    fun provideDisableNfcAllUseCase(
        nfcRepository: NfcRepository
    ): DisableNfcAllUseCase {
        return DisableNfcAllUseCase(nfcRepository)
    }

    @Singleton
    @Provides
    fun provideObserveNfcDataUseCase(
        nfcRepository: NfcRepository
    ): ObserveNfcDataUseCase {
        return ObserveNfcDataUseCase(nfcRepository)
    }

    @Singleton
    @Provides
    fun provideResumeNfcAllUseCase(
        nfcRepository: NfcRepository
    ): ResumeNfcAllUseCase {
        return ResumeNfcAllUseCase(nfcRepository)
    }

    @Singleton
    @Provides
    fun provideIsNfcEnabledUseCase(
        nfcRepository: NfcRepository
    ): IsNfcEnabledUseCase {
        return IsNfcEnabledUseCase(nfcRepository)
    }

    @Singleton
    @Provides
    fun provideIsNfcAvailableUseCase(
        nfcRepository: NfcRepository
    ): IsNfcAvailableUseCase {
        return IsNfcAvailableUseCase(nfcRepository)
    }


    // UseCase
    @Singleton
    @Provides
    fun provideDeleteUserUseCase(
        repository: UserRepository
    ): DeleteUserUseCase {
        return DeleteUserUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetUserByIdUseCase(
        repository: UserRepository
    ): GetUserByIdUseCase {
        return GetUserByIdUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetUserStreamIdUseCase(
        repository: UserRepository
    ): GetUserStreamUseCase {
        return GetUserStreamUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetAllUserStreamIdUseCase(
        repository: UserRepository
    ): GetAllUserStreamUseCase {
        return GetAllUserStreamUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideInsertUserUseCase(
        repository: UserRepository
    ): InsertUserUseCase {
        return InsertUserUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideUpdateUserUseCase(
        repository: UserRepository
    ): UpdateUserUseCase {
        return UpdateUserUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteAccountUseCase(
        repository: SocialMediaRepository
    ): DeleteAccountUseCase {
        return DeleteAccountUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetAccountByUserUseCase(
        repository: SocialMediaRepository
    ): GetAccountByUserUseCase {
        return GetAccountByUserUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetAccountStreamUseCase(
        repository: SocialMediaRepository
    ): GetAccountStreamUseCase {
        return GetAccountStreamUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideInsertAccountUseCase(
        repository: SocialMediaRepository
    ): InsertAccountUseCase {
        return InsertAccountUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideUpdateAccountUseCase(
        repository: SocialMediaRepository
    ): UpdateAccountUseCase {
        return UpdateAccountUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideOpenSocialPlatformUseCase(
        repository: SocialMediaRepository
    ): OpenSocialPlatformUseCase {
        return OpenSocialPlatformUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideUpdateUserAndAccountUseCase(
        userUseCases: UserUseCases,
        socialMediaUseCases: SocialMediaUseCases
    ): UpdateUserAndAccountsUseCase {
        return UpdateUserAndAccountsUseCase(userUseCases, socialMediaUseCases)
    }

    @Singleton
    @Provides
    fun provideCreateTempImageUriUseCase(): CreateTempImageUriUseCase {
        return CreateTempImageUriUseCase()
    }

    @Singleton
    @Provides
    fun provideDeleteProfileImageUseCase(): DeleteProfileImageUseCase {
        return DeleteProfileImageUseCase()
    }

    @Singleton
    @Provides
    fun provideClearTempImageUseCase(): ClearTempProfileImagesUseCase {
        return ClearTempProfileImagesUseCase()
    }

    @Singleton
    @Provides
    fun provideSaveProfileImageUseCase(
        application: Application
    ): SaveProfileImageUseCase {
        return SaveProfileImageUseCase(application)
    }

    @Singleton
    @Provides
    fun provideJsonConverterUseCase(
    ): JsonConverterUseCase {
        return JsonConverterUseCase()
    }

    @Singleton
    @Provides
    fun provideGenerateQrCodeUseCase(
        qrCodeRepository: QrCodeRepository
    ): GenerateQrCodeUse {
        return GenerateQrCodeUse(qrCodeRepository)
    }

    @Singleton
    @Provides
    fun provideParsePhoneNumberUseCase(
    ): ParsePhoneNumberUseCase {
        return ParsePhoneNumberUseCase()
    }

    @Singleton
    @Provides
    fun provideGetDeviceLanguageUseCase(
        localeRepository: LocaleRepository
    ): GetDeviceLocaleUseCase {
        return GetDeviceLocaleUseCase(localeRepository)
    }


    @Singleton
    @Provides
    fun provideUserUseCases(repository: UserRepository): UserUseCases {
        return UserUseCases(
            insertUser = InsertUserUseCase(repository),
            updateUser = UpdateUserUseCase(repository),
            deleteUser = DeleteUserUseCase(repository),
            getUserById = GetUserByIdUseCase(repository),
            getAllUserStream = GetAllUserStreamUseCase(repository),
            getUserStream = GetUserStreamUseCase(repository)
        )
    }

    @Singleton
    @Provides
    fun provideSocialMediaUseCases(repository: SocialMediaRepository): SocialMediaUseCases {
        return SocialMediaUseCases(
            insertAccount = InsertAccountUseCase(repository),
            updateAccount = UpdateAccountUseCase(repository),
            toggleAccountActive = ToggleAccountActiveStatusUseCase(repository),
            deleteAccount = DeleteAccountUseCase(repository),
            getAccountsByUser = GetAccountByUserUseCase(repository),
            openSocialPlatformUseCase = OpenSocialPlatformUseCase(repository),
            getAccountStream = GetAccountStreamUseCase(repository)
        )
    }

    @Singleton
    @Provides
    fun provideStorageUseCases(application: Application): StorageUseCases {
        return StorageUseCases(
            createTempImageUri = CreateTempImageUriUseCase(),
            deleteProfileImage = DeleteProfileImageUseCase(),
            clearTempProfileImages = ClearTempProfileImagesUseCase(),
            saveProfileImage = SaveProfileImageUseCase(application)
        )
    }

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider = ResourceProviderImpl(context)
}