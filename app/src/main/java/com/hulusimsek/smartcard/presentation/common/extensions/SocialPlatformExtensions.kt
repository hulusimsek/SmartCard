package com.hulusimsek.smartcard.presentation.common.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.presentation.theme.facebookBlue
import com.hulusimsek.smartcard.presentation.theme.instagram
import com.hulusimsek.smartcard.presentation.theme.twitterBlue
import com.hulusimsek.smartcard.presentation.theme.whatsappGreen

fun SocialPlatform.iconRes(): Int = when (this) {
    SocialPlatform.INSTAGRAM -> R.drawable.instagram
    SocialPlatform.TWITTER -> R.drawable.twitter
    SocialPlatform.LINKEDIN -> R.drawable.linkedin
    SocialPlatform.FACEBOOK -> R.drawable.facebook
    SocialPlatform.PHONE -> R.drawable.telephone
    SocialPlatform.WHATSAPP -> R.drawable.whatsapp
    SocialPlatform.TELEGRAM -> R.drawable.telegram
    SocialPlatform.YOUTUBE -> R.drawable.youtube
    SocialPlatform.TIKTOK -> R.drawable.tiktok
    SocialPlatform.SNAPCHAT -> R.drawable.snapchat
    SocialPlatform.EMAIL -> R.drawable.email
    SocialPlatform.WEBSITE -> R.drawable.website
}

@Composable
fun SocialPlatform.string(): String = when (this) {
    SocialPlatform.INSTAGRAM -> "INSTAGRAM"
    SocialPlatform.TWITTER -> "TWITTER"
    SocialPlatform.LINKEDIN -> "LINKEDIN"
    SocialPlatform.FACEBOOK -> "FACEBOOK"
    SocialPlatform.PHONE -> stringResource(id = R.string.phone)
    SocialPlatform.WHATSAPP -> "WHATSAPP"
    SocialPlatform.TELEGRAM -> "TELEGRAM"
    SocialPlatform.YOUTUBE -> "YOUTUBE"
    SocialPlatform.TIKTOK -> "TIKTOK"
    SocialPlatform.SNAPCHAT -> "SNAPCHAT"
    SocialPlatform.EMAIL -> "EMAIL"
    SocialPlatform.WEBSITE -> "WEBSITE"
}

fun SocialPlatform.color(): Color = when (this) {
    SocialPlatform.INSTAGRAM -> instagram
    SocialPlatform.TWITTER -> twitterBlue // Twitter blue
    SocialPlatform.FACEBOOK -> facebookBlue // Facebook blue
    SocialPlatform.PHONE -> whatsappGreen // WhatsApp green
    else -> Color.Gray
}