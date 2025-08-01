package com.hulusimsek.smartcard.domain.model

import androidx.annotation.DrawableRes
import com.hulusimsek.smartcard.R

enum class SocialPlatform(
    val displayName: String,
    val baseUrl: String,
    val isPhone: Boolean = false,
    val appScheme: String? = null,
    val packageName: String? = null,
) {
    INSTAGRAM(
        displayName = "Instagram",
        baseUrl = "https://www.instagram.com/",
        appScheme = "instagram://user?username=",
        packageName = "com.instagram.android"
    ),
    TWITTER(
        displayName = "Twitter",
        baseUrl = "https://twitter.com/",
        appScheme = "twitter://user?screen_name=",
        packageName = "com.twitter.android"
    ),
    LINKEDIN(
        displayName = "LinkedIn",
        baseUrl = "https://www.linkedin.com/in/",
        appScheme = "linkedin://profile/",
        packageName = "com.linkedin.android"
    ),
    FACEBOOK(
        displayName = "Facebook",
        baseUrl = "https://www.facebook.com/",
        appScheme = "fb://profile/",
        packageName = "com.facebook.katana"
    ),
    WHATSAPP(
        displayName = "WhatsApp",
        baseUrl = "https://wa.me/",
        appScheme = "https://wa.me/",
        isPhone = true,
        packageName = "com.whatsapp"
    ),
    TELEGRAM(
        displayName = "Telegram",
        baseUrl = "https://t.me/",
        appScheme = "tg://resolve?domain=",
        packageName = "org.telegram.messenger"
    ),
    YOUTUBE(
        displayName = "YouTube",
        baseUrl = "https://www.youtube.com/@",
        appScheme = "vnd.youtube://user/",
        packageName = "com.google.android.youtube"
    ),
    TIKTOK(
        displayName = "TikTok",
        baseUrl = "https://www.tiktok.com/@",
        appScheme = "snssdk1128://user/profile/",
        packageName = "com.zhiliaoapp.musically"
    ),
    SNAPCHAT(
        displayName = "Snapchat",
        baseUrl = "https://www.snapchat.com/add/",
        appScheme = "snapchat://add/",
        packageName = "com.snapchat.android"
    ),
    PHONE(
        displayName = "Phone",
        baseUrl = "tel:",
        isPhone = true,
        appScheme = "tel:",
        packageName = null
    ),
    EMAIL(
        displayName = "Email",
        baseUrl = "mailto:",
        appScheme = "mailto:",
        packageName = null
    ),
    WEBSITE(
        displayName = "Website",
        baseUrl = "https://",
        appScheme = null,
        packageName = null
    )
}

