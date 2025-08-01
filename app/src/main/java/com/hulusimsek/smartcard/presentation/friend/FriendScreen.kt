package com.hulusimsek.smartcard.presentation.friend

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.presentation.friend.components.ReceivedSocialMediaLink
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FriendScreen(
    viewModel: NfcDetailViewModel = hiltViewModel(),
    jsonData: String? = null,
    userId: String? = null,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(jsonData) {
        jsonData?.let {
            viewModel.setReceivedData(it)
        }
    }

    LaunchedEffect(userId) {
        userId?.let {
            viewModel.setData(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event: SocialMediaUiEvent ->
            when (event) {
                is SocialMediaUiEvent.ShowSnackbar -> {
                    val message = event.message
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }

                is SocialMediaUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }

                is SocialMediaUiEvent.ShowSuccess -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.errorMessage!!,
                    color = colorResource(R.color.error_color),
                    fontSize = dimensionResource(id = R.dimen.text_large).value.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.main_theme_color))
                ) {
                    Text(
                        text = stringResource(id = R.string.back),
                        color = Color.White
                    )
                }
            }
        }
        return
    }

    if (uiState.user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Top Bar with Back Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.top_bar_height))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4CAF93),
                                Color(0xFF388E7A) // biraz daha koyu tonu
                            )
                        )
                    )
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            top = dimensionResource(id = R.dimen.padding_large),
                            start = dimensionResource(id = R.dimen.padding_medium)
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = Color.White,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))
                    )
                }
            }

            // Content Card (overlapping)
            Surface(
                shape = RoundedCornerShape(
                    topStart = dimensionResource(id = R.dimen.corner_radius_large),
                    topEnd = dimensionResource(id = R.dimen.corner_radius_large)
                ),
                color = Color.White,
                tonalElevation = dimensionResource(id = R.dimen.tonal_elevation_medium),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.profile_image_size) / 2,
                        start = dimensionResource(id = R.dimen.padding_medium),
                        end = dimensionResource(id = R.dimen.padding_medium),
                        bottom = dimensionResource(id = R.dimen.padding_xlarge)
                    ),
                ) {
                    // Name (Centered)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "${uiState.user!!.firstName} ${uiState.user!!.lastName}".trim(),
                        fontSize = dimensionResource(id = R.dimen.text_xlarge).value.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.title),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))

                    // Received from label
                    Text(
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium)),
                        text = stringResource(id = R.string.received_contact_info),
                        fontSize = dimensionResource(id = R.dimen.text_large).value.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.title)
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

                    // Social Links (LazyColumn)
                    Surface(
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_medium)),
                        tonalElevation = dimensionResource(id = R.dimen.tonal_elevation_small),
                        color = colorResource(R.color.link_background),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                        ) {
                            itemsIndexed(uiState.socialAccounts) { index, account ->
                                ReceivedSocialMediaLink(
                                    account = account,
                                    onClick = {
                                        viewModel.onSocialMediaClicked(
                                            account.platform,
                                            account.username
                                        )
                                    }
                                )

                                if (index < uiState.socialAccounts.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
                                        color = colorResource(R.color.link_divider)
                                    )
                                } else {
                                    Spacer(
                                        modifier = Modifier.height(
                                            dimensionResource(id = R.dimen.button_height) + dimensionResource(
                                                id = R.dimen.padding_xlarge
                                            ) * 2
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Profile Photo (overlapping) - Default placeholder since no image data
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.profile_image_size))
                .align(Alignment.TopCenter)
                .offset(y = dimensionResource(id = R.dimen.profile_photo_offset))
                .clip(CircleShape)
                .background(Color.White)
                .padding(dimensionResource(id = R.dimen.padding_xsmall))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_placeholder_profile),
                contentDescription = stringResource(id = R.string.profile_photo),
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize()
                    .background(colorResource(R.color.placeholder_background)),
                contentScale = ContentScale.Crop
            )
        }

        // Save Contact Button
        Button(
            //viewModel.saveContact()
            onClick = {
                if(uiState.isFromDB && uiState.user != null) {
                    showDeleteDialog = true
                }
                else {
                    viewModel.saveNewProfile(onNavigateBack)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.main_theme_color)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(id = R.dimen.padding_large))
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.button_height)),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_rounded)),
        ) {
            if(uiState.isFromDB && uiState.user != null) {
                Icon(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = stringResource(id = R.string.delete_user),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.delete_icon)),
                )
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
                Text(
                    stringResource(id = R.string.delete_user),
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
            else {
                Icon(
                    painter = painterResource(id = R.drawable.add_user),
                    contentDescription = stringResource(id = R.string.save_contact),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
                Text(
                    stringResource(id = R.string.save_contact),
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

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
                            "${uiState.user?.firstName ?: ""} ${uiState.user?.lastName ?: ""}".trim()
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteUser(uiState.user, onBack = onNavigateBack)
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
}
