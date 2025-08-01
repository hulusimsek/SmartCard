package com.hulusimsek.smartcard.presentation.all_user.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.core.util.TimeUtils
import com.hulusimsek.smartcard.domain.model.User

@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_medium)),
        tonalElevation = dimensionResource(id = R.dimen.tonal_elevation_small),
        color = colorResource(R.color.link_background),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profil fotoğrafı
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
            ) {
                AsyncImage(
                    model = user.profileImagePath,
                    contentDescription = stringResource(id = R.string.profile_photo),
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_placeholder_profile),
                    error = painterResource(id = R.drawable.ic_placeholder_profile),
                    fallback = painterResource(id = R.drawable.ic_placeholder_profile)
                )
            }

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

            // Kullanıcı bilgileri
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim()
                        .ifEmpty { stringResource(id = R.string.unknown_user) },
                    fontSize = dimensionResource(id = R.dimen.text_medium).value.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Eklenme tarihi
                Text(
                    text = stringResource(
                        id = R.string.added_date,
                        TimeUtils.formatDateForDisplay(user.createdAt ?: "")
                    ),
                    fontSize = dimensionResource(id = R.dimen.text_small).value.sp,
                    color = colorResource(R.color.title).copy(alpha = 0.5f)
                )
            }

            // Sil butonu
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete_user),
                    tint = colorResource(R.color.delete_button_color),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Silme onay dialogu
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = stringResource(id = R.string.delete_user_title))
            },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.delete_user_message,
                        "${user.firstName ?: ""} ${user.lastName ?: ""}".trim()
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.delete),
                        color = colorResource(R.color.error_color)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = colorResource(R.color.main_theme_color)
                    )
                }
            }
        )
    }
}