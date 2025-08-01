package com.hulusimsek.smartcard.presentation.all_user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.core.util.TimeUtils
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.presentation.all_user.components.AnimatedSearchBar
import com.hulusimsek.smartcard.presentation.all_user.components.UserItem


@Composable
fun AllUsersScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: AllUsersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Her tıklamada focus'u temizle
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    viewModel.closeSearchMode()
                })
            })
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Top Bar (HomeScreen ile tutarlı)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.account_top_bar_height))
                    .background(colorResource(id = R.color.main_theme_color)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.padding_medium),
                            vertical = dimensionResource(id = R.dimen.padding_small)
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if(!uiState.isSearchMode) {
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                                tint = Color.White
                            )
                        }
                    }


                    AnimatedSearchBar(
                        searchQuery = searchQuery,
                        isSearchMode = uiState.isSearchMode,
                        clearSearch = { viewModel.clearSearch() },
                        updateSearchQuery = { viewModel.updateSearchQuery(it) }
                    )

                    if(!uiState.isSearchMode) {
                        Text(
                            text = stringResource(id = R.string.all_users),
                            fontSize = dimensionResource(id = R.dimen.text_large).value.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        // Arama ikonu
                        IconButton(
                            onClick = { viewModel.toggleSearchMode() }
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.search),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Content Card (HomeScreen tarzında)
            Surface(
                shape = RoundedCornerShape(
                    topStart = dimensionResource(id = R.dimen.corner_radius_large),
                    topEnd = dimensionResource(id = R.dimen.corner_radius_large)
                ),
                color = Color.White,
                tonalElevation = dimensionResource(id = R.dimen.tonal_elevation_medium),
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = dimensionResource(id = R.dimen.account_screen_offset)),
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.padding_medium),
                        start = dimensionResource(id = R.dimen.padding_medium),
                        end = dimensionResource(id = R.dimen.padding_medium),
                        bottom = dimensionResource(id = R.dimen.padding_medium)
                    )
                ) {
                    // Kullanıcı sayısı bilgisi
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.users_count,
                            count = uiState.filteredUsers.size,
                            uiState.filteredUsers.size
                        ),
                        fontSize = dimensionResource(id = R.dimen.text_medium).value.sp,
                        color = colorResource(R.color.title).copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_small))
                    )

                    // Loading durumu
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = colorResource(id = R.color.main_theme_color)
                            )
                        }
                    }
                    // Error durumu
                    else if (uiState.error != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = colorResource(R.color.error_color),
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

                                Text(
                                    text = uiState.error!!,
                                    color = colorResource(R.color.error_color),
                                    textAlign = TextAlign.Center
                                )

                            }
                        }
                    }
                    // Kullanıcı listesi
                    else if (uiState.filteredUsers.isEmpty()) {
                        // Boş durum
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.nothing),
                                    contentDescription = null,
                                    tint = colorResource(R.color.title).copy(alpha = 0.5f),
                                    modifier = Modifier.size(64.dp)
                                )

                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

                                Text(
                                    text = if (uiState.isSearchMode && searchQuery.isNotEmpty()) {
                                        stringResource(id = R.string.no_users_found)
                                    } else {
                                        stringResource(id = R.string.no_saved_users)
                                    },
                                    color = colorResource(R.color.title).copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    fontSize = dimensionResource(id = R.dimen.text_medium).value.sp
                                )
                            }
                        }
                    } else {
                        // Kullanıcı listesi
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                        ) {
                            items(
                                items = uiState.filteredUsers.toList(),
                                key = { user -> user.id }
                            ) { user ->
                                UserItem(
                                    user = user,
                                    onClick = {
                                        navController.navigate("user_detail/${user.id}")
                                    },
                                    onDelete = {
                                        viewModel.deleteUser(user)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
