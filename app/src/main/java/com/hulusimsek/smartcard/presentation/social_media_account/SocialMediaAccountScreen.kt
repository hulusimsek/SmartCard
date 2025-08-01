package com.hulusimsek.smartcard.presentation.social_media_account

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.presentation.common.extensions.iconRes
import com.hulusimsek.smartcard.presentation.common.extensions.string
import com.hulusimsek.smartcard.presentation.social_media_account.components.CountryDropdown
import com.hulusimsek.smartcard.presentation.social_media_account.components.SocialPlatformDropdown
import com.hulusimsek.smartcard.presentation.social_media_account.components.ThreeThreeFourTransformation
import com.hulusimsek.smartcard.core.extension.capitalizeFirst
import com.hulusimsek.smartcard.core.extension.formatAsSmartPhone
import com.hulusimsek.smartcard.presentation.social_media_account.components.CustomOutlinedTextField

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun SocialMediaAccountScreen(
    navController: NavController,
    viewModel: SocialMediaAccountViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val currentAccount by viewModel.currentAccount.collectAsState()
    val country by viewModel.country.collectAsState()
    val state by viewModel.uiState.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val backStackEntry =
        remember { navController.getBackStackEntry("edit") }
    val savedStateHandle = backStackEntry.savedStateHandle

    LaunchedEffect(Unit) {
        savedStateHandle
            .getLiveData<String?>("editingAccount")
            .observe(lifecycleOwner) { json ->
                // Json verisini ViewModel'e ilet
                if (!json.isNullOrEmpty()) {
                    viewModel.processNavigationResult(json)
                }

                // Temizle - UI katmanında gerçekleştirilir
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<String?>("editingAccount")
            }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToEditScreen.collect { account ->
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("socialMediaResult", account)
            navController.popBackStack()
        }
    }

    BackHandler {
        onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Her tıklamada focus'u temizle
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            })
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Üst kısım
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.account_top_bar_height))
                    .background(colorResource(R.color.main_theme_color)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.padding_medium),
                            vertical = dimensionResource(id = R.dimen.padding_small)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))

                    Text(
                        text = stringResource(id = R.string.add_social_media_account),
                        color = Color.White,
                        fontSize = dimensionResource(id = R.dimen.text_large).value.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // İçerik kısmı
            Surface(
                shape = RoundedCornerShape(
                    topStart = dimensionResource(id = R.dimen.corner_radius_large),
                    topEnd = dimensionResource(id = R.dimen.corner_radius_large)
                ),
                color = Color.White,
                tonalElevation = dimensionResource(id = R.dimen.tonal_elevation_medium),
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = dimensionResource(id = R.dimen.account_screen_offset))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.padding_large),
                            vertical = dimensionResource(id = R.dimen.padding_xlarge)
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.select_platform),
                        fontWeight = FontWeight.Medium,
                        fontSize = dimensionResource(id = R.dimen.text_medium).value.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

                    // Platform seçimi
                    SocialPlatformDropdown(
                        selectedPlatform = state.platform,
                        onPlatformSelected = { viewModel.onPlatformSelected(it) }
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))

                    if (state.platform.isPhone) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            CountryDropdown(
                                selectedCountries = country,
                                countryCodeChanged = { viewModel.countryCodeChanged(it) },
                                modifier = Modifier
                                    .width(dimensionResource(id = R.dimen.country_dropdown_width))
                            )


                            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))

                            // Telefon numarası giriş alanı
                            CustomOutlinedTextField(
                                value = state.username,
                                onValueChange = { viewModel.onUsernameChanged(it) },
                                label = stringResource(id = R.string.phone_number),
                                modifier = Modifier
                                    .padding(0.dp)
                                    .weight(1f),
                                isError = !state.isSaveEnabled,
                                supportingText = if (!state.isSaveEnabled) stringResource(id = R.string.enter_valid_phone_number) else null,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone
                                ),
                                paddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_medium)),
                                visualTransformation = ThreeThreeFourTransformation()
                            )
                        }

                    } else {
                        CustomOutlinedTextField(
                            value = state.username,
                            onValueChange = { viewModel.onUsernameChanged(it) },
                            label = stringResource(id = R.string.username),
                            modifier = Modifier.fillMaxWidth(),
                            isError = !state.isSaveEnabled,
                            supportingText = if (!state.isSaveEnabled) stringResource(id = R.string.username_required) else null,
                            paddingValues = PaddingValues(dimensionResource(id = R.dimen.padding_medium)),
                            prefix = {
                                AsyncImage(
                                    model = state.platform.iconRes(),
                                    contentDescription = state.platform.name,
                                    modifier = Modifier.size(dimensionResource(id = R.dimen.edit_icon_button_size))
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = viewModel::onDescriptionChanged,
                        label = {
                            Text(
                                stringResource(id = R.string.description_optional),
                                color = colorResource(R.color.text_color)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

                    // Platform özellikleri
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.social_account_card_color)
                        )
                    ) {
                        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                            Text(
                                text = stringResource(
                                    id = R.string.platform_account,
                                    state.platform.string().capitalizeFirst()
                                ),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = state.platform.iconRes(),
                                    contentDescription = state.platform.name,
                                    modifier = Modifier.size(dimensionResource(id = R.dimen.edit_icon_button_size))
                                )
                                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))

                                if (state.username.isNotBlank()) {
                                    if (state.platform.isPhone) {
                                        Text("+${country.phoneCode} ${state.username.formatAsSmartPhone()}")
                                    } else {
                                        Text("@${state.username}")
                                    }
                                } else {
                                    Text(
                                        text = stringResource(id = R.string.enter_username),
                                        fontStyle = FontStyle.Italic,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(id = R.dimen.padding_xlarge))
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth()
        ) {
            Column {
                Button(
                    onClick = {
                        viewModel.addAccount()
                    },
                    enabled = state.isSaveEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.button_height)),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_rounded)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.main_theme_color),
                        disabledContainerColor = colorResource(R.color.disabled_container_color), // Butonun arka plan rengi (pasif)
                        disabledContentColor = colorResource(R.color.disabled_content_color) // Buton içindeki metin veya ikonun rengi (pasif)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.add_account),
                        color = Color.White,
                        fontSize = dimensionResource(id = R.dimen.text_medium).value.sp
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.button_height)),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_rounded)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(R.color.main_theme_color)
                    ),
                    border = BorderStroke(
                        dimensionResource(id = R.dimen.button_border),
                        colorResource(R.color.main_theme_color)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        fontSize = dimensionResource(id = R.dimen.text_medium).value.sp
                    )
                }
            }

        }

    }
}