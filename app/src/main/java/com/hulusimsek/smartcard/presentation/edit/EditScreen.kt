package com.hulusimsek.smartcard.presentation.edit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.presentation.common.extensions.getFloatDimen
import com.hulusimsek.smartcard.presentation.common.extensions.iconRes
import com.hulusimsek.smartcard.presentation.common.extensions.string

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    navController: NavController,
    onBack: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val socialAccounts by viewModel.socialAccounts.collectAsState()

    val tempUserInfo by viewModel.tempUserInfo.collectAsState()

    var showImagePickerDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val profileImageSize = dimensionResource(id = R.dimen.profile_image_size)

    val context = LocalContext.current

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.e("izinler", "launcher uri")
        uri?.let {
            viewModel.updateTempProfilImage(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Log.e("izinler", "cameraLauncher success")
            viewModel.tempImageUri?.let { uri ->
                viewModel.updateTempProfilImage(uri)
            }
        } else {
            Log.e("izinler", "cameraLauncher success else")
        }
    }

    // İzin isteme için
    val cameraPermissionRequired = stringResource(id = R.string.camera_permission_required)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.e("izinler", "permissionLauncher isGranted")
            viewModel.createTempImageUri(context)?.let { uri ->
                viewModel.tempImageUri = uri
                cameraLauncher.launch(uri)
            }
        } else {
            Log.e("izinler", "permissionLauncher isGranted else")
            Toast.makeText(context, cameraPermissionRequired, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(user, socialAccounts) {
        if (user != null && socialAccounts != null) {
            viewModel.initializeTempData(user, socialAccounts!!)
        }
    }

    if (tempUserInfo == null) {
        // Henüz veri yüklenmediyse loading göster
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String?>("socialMediaResult")
            ?.observe(lifecycleOwner) { json ->
                // Json verisini ViewModel'e ilet
                if (!json.isNullOrEmpty()) {
                    viewModel.processNavigationResult(json)
                }

                // Temizle - UI katmanında gerçekleştirilir
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<String?>("socialMediaResult")
            }
    }

    LaunchedEffect(Unit) {
        Log.e("cleanmimari", "LaunchedEffect observe inital")

        viewModel.navigateToAccountScreen.collect { account ->
            Log.e("cleanmimari", "MutableSharedFlow observe triggered")
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("editingAccount", account)

            navController.navigate("social_media_account")
        }
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text(stringResource(id = R.string.select_profile_photo)) },
            text = { Text(stringResource(id = R.string.where_select_photo)) },
            confirmButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    launcher.launch("image/*")
                }) {
                    Text(stringResource(id = R.string.select_from_gallery))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        viewModel.createTempImageUri(context)?.let { uri ->
                            viewModel.tempImageUri = uri
                            Log.e("izinler", "cameraLauncherUri: $uri")
                            cameraLauncher.launch(uri)
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text(stringResource(id = R.string.take_photo))
                }
            }
        )
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
            }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.top_bar_height))
                    .background(colorResource(R.color.main_theme_color))
            )

            Surface(
                shape = RoundedCornerShape(
                    topStart = dimensionResource(id = R.dimen.corner_radius_large),
                    topEnd = dimensionResource(id = R.dimen.corner_radius_large)
                ),
                color = Color.White,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = profileImageSize / 2,
                        start = dimensionResource(id = R.dimen.padding_medium),
                        end = dimensionResource(id = R.dimen.padding_medium),
                        bottom = dimensionResource(id = R.dimen.padding_xlarge) * 2 + dimensionResource(
                            id = R.dimen.padding_small
                        )
                    ),
                ) {
                    OutlinedTextField(
                        value = tempUserInfo!!.firstName,
                        onValueChange = { viewModel.updateTempFirstName(it) },
                        label = {
                            Text(
                                stringResource(id = R.string.first_name),
                                color = Color.Black
                            )
                        },
                        isError = tempUserInfo!!.firstName.isBlank(),
                        supportingText = {
                            if (tempUserInfo!!.firstName.isBlank())
                                Text(stringResource(id = R.string.first_name_required))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

                    OutlinedTextField(
                        value = tempUserInfo!!.lastName,
                        onValueChange = { viewModel.updateTempLastName(it) },
                        label = { Text(stringResource(id = R.string.last_name), color = colorResource(R.color.text_color)) },
                        isError = tempUserInfo!!.lastName.isBlank(),
                        supportingText = {
                            if (tempUserInfo!!.lastName.isBlank())
                                Text(stringResource(id = R.string.last_name_required))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.social_media_accounts),
                            fontSize = dimensionResource(id = R.dimen.text_normal).value.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.onEditAccountClick(null) }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.add_account),
                                tint = colorResource(R.color.add_button_color)
                            )
                        }
                    }

                    LazyColumn {
                        items(tempUserInfo!!.socialAccounts) { account ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimensionResource(id = R.dimen.padding_small))
                            ) {
                                AsyncImage(
                                    model = account.platform.iconRes(),
                                    contentDescription = account.platform.name,
                                    modifier = Modifier.size(dimensionResource(id = R.dimen.account_icon_size))
                                )
                                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.social_media_spacing)))
                                Text(
                                    text = account.platform.string(),
                                    fontSize = dimensionResource(id = R.dimen.text_medium).value.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (!account.description.isNullOrBlank()) {
                                    Text(
                                        text = account.description,
                                        fontSize = dimensionResource(id = R.dimen.text_small_2).value.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f).padding(horizontal = dimensionResource(id = R.dimen.padding_small))
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                                Switch(
                                    checked = account.isActive,
                                    modifier = Modifier.scale(context.getFloatDimen(R.dimen.switch_scale)),
                                    colors = SwitchColors(
                                        // Checked (ON) durumu
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = colorResource(R.color.main_theme_color),
                                        checkedBorderColor = colorResource(R.color.main_theme_color),
                                        checkedIconColor = Color.Transparent,

                                        // Unchecked (OFF) durumu
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f),
                                        uncheckedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                        uncheckedIconColor = Color.Transparent,

                                        // Disabled checked durumu
                                        disabledCheckedThumbColor = Color.White.copy(alpha = 0.6f),
                                        disabledCheckedTrackColor = colorResource(R.color.main_theme_color).copy(
                                            alpha = 0.4f
                                        ),
                                        disabledCheckedBorderColor = colorResource(R.color.main_theme_color).copy(
                                            alpha = 0.4f
                                        ),
                                        disabledCheckedIconColor = Color.Transparent,

                                        // Disabled unchecked durumu
                                        disabledUncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                                        disabledUncheckedTrackColor = Color.Gray.copy(alpha = 0.3f),
                                        disabledUncheckedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                        disabledUncheckedIconColor = Color.Transparent
                                    ),
                                    onCheckedChange = {
                                        viewModel.toggleTempSocialAccount(account)
                                    }
                                )

                                IconButton(
                                    modifier = Modifier.size(dimensionResource(id = R.dimen.edit_icon_button_size)),
                                    onClick = {
                                        Log.e("cleanmimari", "IconButton triggered")
                                        viewModel.onEditAccountClick(account)
                                    }) {
                                    Image(
                                        painter = painterResource(id = R.drawable.edit),
                                        contentDescription = stringResource(id = R.string.edit),
                                        modifier = Modifier.size(dimensionResource(id = R.dimen.edit_icon_size)),
                                    )
                                }

                                IconButton(onClick = {
                                    viewModel.deleteTempSocialAccount(account)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(id = R.string.delete),
                                        tint = colorResource(R.color.delete_button_color)
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
                .size(profileImageSize)
                .align(Alignment.TopCenter)
                .offset(y = dimensionResource(id = R.dimen.profile_offset_y))
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
                .clickable {
                    showImagePickerDialog = true
                }
        ) {
            AsyncImage(
                model = tempUserInfo!!.profileImageUri,
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

        Button(
            onClick = {
                viewModel.persistChanges()
                onBack()
            },
            enabled = tempUserInfo!!.firstName.isNotBlank() && tempUserInfo!!.lastName.isNotBlank(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(id = R.dimen.padding_large))
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
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
                text = stringResource(id = R.string.save),
                color = Color.White,
                fontSize = dimensionResource(id = R.dimen.text_medium).value.sp
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Sayfa (Composable) yok oluyorsa burada temizlik işlemleri yapılır
            viewModel.clearTempProfileImages(context)  // Örnek: geçici profil fotoğrafı silme
        }
    }
}