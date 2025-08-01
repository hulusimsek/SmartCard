package com.hulusimsek.smartcard.presentation.home

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.domain.model.NfcMode
import com.hulusimsek.smartcard.domain.model.NfcStatus
import com.hulusimsek.smartcard.presentation.home.components.NfcSharingDialog
import com.hulusimsek.smartcard.presentation.home.components.QrCodeDialog
import com.hulusimsek.smartcard.presentation.home.components.ShareOptionBottomSheet
import com.hulusimsek.smartcard.presentation.home.components.SocialMediaLink
import com.hulusimsek.smartcard.presentation.qr.CaptureActivityPortrait
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {


    val uiState by viewModel.uiState.collectAsState()
    val nfcData by viewModel.nfcData.collectAsState()

    val deepLinkData by viewModel.deepLinkData.collectAsState()

    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.resumeNfc()
                    Log.d("nfcdeneme", "onResume - Ekran tekrar aktif")
                    Log.e("deneme", uiState.activeSocialAccounts.toString())
                    // Örn: NFC'yi aktif et
                }

                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.disableNfcAll()
                    Log.d("nfcdeneme", "onPause - Ekran geçici olarak görünmez")
                    // Örn: NFC'yi duraklat
                }

                Lifecycle.Event.ON_START -> {
                    viewModel.resumeNfc()
                    Log.d("nfcdeneme", "onStart - Görünür olmaya başladı")
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.disableNfcAll()
                    Log.d("nfcdeneme", "onStop - Artık görünmüyor")
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            viewModel.disableNfcAll()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(nfcData) {
        if (!nfcData.isNullOrEmpty()) {
            navController.navigate("nfc/${nfcData}") {
                launchSingleTop = true
            }
            viewModel.clearNfcData()
        }
    }

    LaunchedEffect(uiState.qrCodeError) {
        val message = uiState.qrCodeError
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val qrScanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        result?.contents?.let {
            viewModel.onQrScanned(it)
        }
    }

    if (uiState.isLoading || uiState.user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    uiState.qrCodeData?.let { qrCodeData ->
        QrCodeDialog(
            qrCodeData = qrCodeData,
            onDismiss = { viewModel.clearQrCode() }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Top Bar (Düz renk)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.top_bar_height))
                    .background(colorResource(id = R.color.main_theme_color))
            ) {
                Row(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_medium))
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.all_user_button_size)),
                        onClick = {
                            navController.navigate("all_users") // Rotanı buraya yaz
                            Log.e("allUser", "IconButton triggered")
                        }) {
                        Image(
                            painter = painterResource(id = R.drawable.all_user),
                            contentDescription = stringResource(id = R.string.all_user),
                            modifier = Modifier.size(dimensionResource(id = R.dimen.all_user_icon_size)),
                        )
                    }
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
                modifier = Modifier
                    .fillMaxSize()
                //.offset(y = (-24).dp)
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.profile_image_size) / 2,
                        start = dimensionResource(id = R.dimen.padding_medium),
                        end = dimensionResource(id = R.dimen.padding_medium),
                        bottom = dimensionResource(id = R.dimen.padding_xlarge)
                    ),
                ) {
                    // Name and Job (Ortalanmış)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "${uiState.user!!.firstName ?: stringResource(id = R.string.user_name)} ${
                            uiState.user!!.lastName ?: stringResource(
                                id = R.string.user_surname
                            )
                        }".trim(),
                        fontSize = dimensionResource(id = R.dimen.text_xlarge).value.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.title),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))

                    Text(
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium)),
                        text = stringResource(id = R.string.links),
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
                                SocialMediaLink(
                                    account = account,
                                    { viewModel.onSocialMediaLinkClicked(account) })

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

        // Profile Photo (overlapping)
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.profile_image_size))
                .align(Alignment.TopCenter)
                .offset(y = dimensionResource(id = R.dimen.profile_photo_offset))
                .clip(CircleShape)
                .background(Color.White)
                .padding(dimensionResource(id = R.dimen.padding_xsmall))
        ) {
            Log.e("izinler", "veritabanı fotoğrafı: ${uiState.user!!.profileImagePath}")
            AsyncImage(
                model = uiState.user!!.profileImagePath,
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

        FloatingActionButton(
            onClick = { navController.navigate("edit") },
            containerColor = colorResource(id = R.color.main_theme_color),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = dimensionResource(id = R.dimen.padding_large),
                    bottom = dimensionResource(id = R.dimen.padding_xlarge) * 3 + dimensionResource(
                        id = R.dimen.padding_small
                    ) // NFC butonuyla çakışmaması için yukarıda
                )
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit),
                tint = Color.White
            )
        }

        // Fixed NFC Button (Bottom) — Görünür ve sabit
        Button(
            onClick = { viewModel.showBottomSheetVisible() },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.main_theme_color),
                disabledContainerColor = colorResource(R.color.disabled_container_color), // Butonun arka plan rengi (pasif)
                disabledContentColor = colorResource(R.color.disabled_content_color) // Buton içindeki metin veya ikonun rengi (pasif)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(id = R.dimen.padding_xlarge))
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.button_height)),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_rounded)),
        ) {

            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "NFC", tint = Color.White)
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
            Text(
                stringResource(id = R.string.share_button),
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }

        if (uiState.isShowBottomSheetVisible) {
            ShareOptionBottomSheet(
                onDismiss = { viewModel.dismissShowBottomSheet() },
                onStartNfc = { /* NFC dinlemeyi başlat */ },
                onShareViaNfc = { viewModel.sendUserDataNfc() },
                onOpenQrCamera = { qrScanLauncher.launch(
                    ScanOptions().apply {
                        setPrompt("Scan QR code")
                        setBeepEnabled(true)
                        setOrientationLocked(true)
                        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        setCameraId(0)
                        setCaptureActivity(CaptureActivityPortrait::class.java)
                    }
                ) },
                onShareViaQr = { viewModel.generateQrCodeForUserData() },
                isGeneratingQrCode = uiState.isGeneratingQrCode,
                nfcReader = uiState.nfcStatus == NfcStatus.ENABLED && uiState.nfcMode == NfcMode.LISTENER,
                nfcEnabled = uiState.nfcStatus == NfcStatus.ENABLED,
                onToggleNfc = { enabled -> viewModel.toggleNfc(enabled) }
            )
        }

        if (uiState.isNfcSharingDialogVisible) {
            NfcSharingDialog(
                onDismiss = { viewModel.dismissNfcDialog() }
            )
        }
    }
}

