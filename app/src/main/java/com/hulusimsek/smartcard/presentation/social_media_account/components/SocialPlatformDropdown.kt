package com.hulusimsek.smartcard.presentation.social_media_account.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.domain.model.SocialPlatform
import com.hulusimsek.smartcard.presentation.common.extensions.iconRes
import com.hulusimsek.smartcard.presentation.common.extensions.string

@Composable
fun SocialPlatformDropdown(
    selectedPlatform: SocialPlatform,
    onPlatformSelected: (SocialPlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Column {
        Box {
            OutlinedTextField(
                value = selectedPlatform.string(),
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(id = R.string.social_platform), color = colorResource(R.color.text_color)) },
                enabled = true, // Etkin olduğundan emin olun
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                leadingIcon = {
                    AsyncImage(
                        model = selectedPlatform.iconRes(),
                        contentDescription = selectedPlatform.displayName,
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.social_icon))
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(id = R.string.dropdown)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        indication = null, // Tıklama efektini kaldıralım
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) {
                        expanded = true // Tıklandığında expanded true yap
                    }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                SocialPlatform.entries.forEach { platform ->
                    DropdownMenuItem(
                        text = { Text(text = platform.string()) },
                        onClick = {
                            onPlatformSelected(platform)
                            expanded = false
                        },
                        leadingIcon = {
                            AsyncImage(
                                model = platform.iconRes(),
                                contentDescription = platform.displayName,
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.social_icon))
                            )
                        }
                    )
                }
            }
        }
    }
}