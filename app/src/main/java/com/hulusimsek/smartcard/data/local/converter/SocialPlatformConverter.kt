package com.hulusimsek.smartcard.data.local.converter

import androidx.room.TypeConverter
import com.hulusimsek.smartcard.domain.model.SocialPlatform


class SocialPlatformConverter {

    @TypeConverter
    fun fromSocialPlatform(platform: SocialPlatform): String {
        return platform.name
    }

    @TypeConverter
    fun toSocialPlatform(value: String): SocialPlatform {
        return SocialPlatform.valueOf(value)
    }
}