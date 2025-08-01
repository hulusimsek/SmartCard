package com.hulusimsek.smartcard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hulusimsek.smartcard.data.local.converter.SocialPlatformConverter
import com.hulusimsek.smartcard.data.local.dao.SocialMediaAccountDao
import com.hulusimsek.smartcard.data.local.dao.UserDao
import com.hulusimsek.smartcard.data.local.entity.SocialMediaAccountEntity
import com.hulusimsek.smartcard.data.local.entity.UserEntity


@Database(
    entities = [UserEntity::class, SocialMediaAccountEntity::class],
    version = 4
)
@TypeConverters(SocialPlatformConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun socialMediaAccountDao(): SocialMediaAccountDao
}