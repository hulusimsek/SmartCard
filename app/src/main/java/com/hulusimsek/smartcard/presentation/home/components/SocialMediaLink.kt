package com.hulusimsek.smartcard.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.presentation.common.extensions.iconRes
import com.hulusimsek.smartcard.presentation.common.extensions.string

@Composable
fun SocialMediaLink(account: SocialMediaAccount, onClick: () -> Unit = {}) {
    val isActive = account.isActive

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(id = R.dimen.padding_small))
            .alpha(if (isActive) 1f else 0.5f), // Opacity azaltma (pasif)
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = account.platform.iconRes(),
            contentDescription = account.platform.name,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.social_media_icon_size))
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.social_media_spacing)))

        Text(
            text = account.platform.string(),
            fontSize = dimensionResource(id = R.dimen.text_normal).value.sp,
            color = if (isActive) Color(0xFF212121) else Color.Gray, // Renk farkı
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
        )

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.social_media_spacing)))

        if (!account.description.isNullOrBlank()) {
            Text(
                text = account.description,
                fontSize = dimensionResource(id = R.dimen.text_small_2).value.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f) // <--- BURASI: ağırlık ver
            )
        } else {
            Spacer(modifier = Modifier.weight(1f)) // <--- Description yoksa Spacer koy ki Dot sağda kalsın
        }

        // Sağda yeşil aktif dot (Aktifse göster)
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.dot_indicator_size))
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)) // Yeşil renk
            )
        }
    }
}