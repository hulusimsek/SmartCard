package com.hulusimsek.smartcard.presentation.friend.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.domain.model.SocialMediaAccount
import com.hulusimsek.smartcard.presentation.common.extensions.iconRes

@Composable
fun ReceivedSocialMediaLink(
    account: SocialMediaAccount,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(id = R.dimen.padding_small)),
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = account.platform.name,
                fontSize = dimensionResource(id = R.dimen.text_normal).value.sp,
                color = colorResource(R.color.title),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = account.username,
                fontSize = dimensionResource(id = R.dimen.text_small_2).value.sp,
                color = colorResource(R.color.received_username_color),
                fontWeight = FontWeight.Medium
            )

            if (!account.description.isNullOrBlank()) {
                Text(
                    text = account.description,
                    fontSize = dimensionResource(id = R.dimen.text_small).value.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Action icon
        Icon(
            painter = painterResource(id = R.drawable.link),
            contentDescription = stringResource(id = R.string.open_link),
            tint = colorResource(R.color.main_theme_color),
            modifier = Modifier.size(dimensionResource(id = R.dimen.action_icon_size))
        )
    }
}