package com.hulusimsek.smartcard.presentation.social_media_account.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.domain.model.Countries

@Composable
fun CountryDropdown(
    selectedCountries: Countries,
    countryCodeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Kod içeriğini hafızada tutuyoruz
    val codeText = remember { mutableStateOf(selectedCountries.phoneCode) }

    CustomOutlinedTextField(
        value = codeText.value,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        onValueChange = {
            val filtered = it.filter { ch -> ch.isDigit() || ch == '-' }
            codeText.value = filtered
            countryCodeChanged(filtered)
        },
        label = stringResource(id = R.string.code),
        labelColorUnfocused = colorResource(R.color.text_color),
        labelColorFocused = colorResource(R.color.text_color),
        prefix = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_xsmall))
            ) {
                AsyncImage(
                    model = "https://flagsapi.com/${selectedCountries.code}/flat/64.png",
                    contentDescription = selectedCountries.code,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.country_image))
                )
                Text("+")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

//    Box(modifier = modifier) {
//        OutlinedTextField(
//            value = codeText.value,
//            onValueChange = { newText ->
//                val filtered = newText.filter { it.isDigit() }
//                codeText.value = filtered
//                countryCodeChanged(filtered)
//            },
//            label = { Text("Kod") },
//            textStyle = MaterialTheme.typography.bodyMedium.copy(
//                textAlign = TextAlign.Start
//            ),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedTextColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
//            ),
//            leadingIcon = {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier.padding(0.dp)
//                ) {
//                    AsyncImage(
//                            model = "https://flagsapi.com/${selectedCountries.code}/flat/64.png",
//                            contentDescription = selectedCountries.name,
//                            modifier = Modifier.size(20.dp)
//                        )
//
//                    Text(
//                        text = "+",
//                        style = MaterialTheme.typography.bodyMedium,
//                        modifier = Modifier.padding(start = 4.dp)
//                    )
//                }
//            },
////            trailingIcon = {
////                Icon(
////                    imageVector = Icons.Filled.ArrowDropDown,
////                    contentDescription = "Dropdown",
////                    modifier = Modifier.size(20.dp).clickable { expanded = true }
////                )
////            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .onGloballyPositioned { coordinates ->
//                    textFieldSize = coordinates.size.toSize()
//                },
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number
//            ),
//            singleLine = true
//        )
//
//        // Dropdown açılması için gerekli clickable alan
////        DropdownMenu(
////            expanded = expanded,
////            onDismissRequest = { expanded = false },
////            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
////        ) {
////            Countries.entries.forEach { country ->
////                DropdownMenuItem(
////                    text = {
////                        Row(verticalAlignment = Alignment.CenterVertically) {
////                            Text(
////                                text = "+${country.phoneCode} ${country.name}",
////                                fontSize = 12.sp,
////                                maxLines = 1,
////                                overflow = TextOverflow.Ellipsis
////                            )
////                        }
////                    },
////                    onClick = {
////                        onCountrySelected(country)
////                        codeText.value = country.phoneCode
////                        expanded = false
////                    },
////                    leadingIcon = {
////                        AsyncImage(
////                            model = "https://flagsapi.com/${country.code}/flat/64.png",
////                            contentDescription = country.name,
////                            modifier = Modifier.size(20.dp)
////                        )
////                    }
////                )
////            }
////        }
//    }
//}