package com.hulusimsek.smartcard.presentation.all_user.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.presentation.theme.textColor


@Composable
fun AnimatedSearchBar(
    searchQuery: String,
    isSearchMode: Boolean,
    clearSearch: () -> Unit,
    updateSearchQuery: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    AnimatedVisibility(
        visible = isSearchMode,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {

        LaunchedEffect(isSearchMode) {
            if (isSearchMode) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }

        Surface(
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_medium)),
            tonalElevation = dimensionResource(id = R.dimen.tonal_elevation_small),
            border = BorderStroke(1.dp, colorResource(R.color.link_background)),
            color = Color.Transparent.copy(alpha = 0.2F),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_medium))
        ) {

            TextField(
                value = searchQuery,
                onValueChange = { updateSearchQuery(it) },
                placeholder = {
                    Text(text = stringResource(id = R.string.search_users), color = Color.White.copy(alpha = 0.6F))
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { clearSearch() }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.clear_search),
                                tint = Color.White
                            )
                        }
                    }
                },
                textStyle = TextStyle(color = Color.White),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                singleLine = true
            )
        }
    }
}