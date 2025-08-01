package com.hulusimsek.smartcard.presentation.social_media_account.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    prefix: @Composable (() -> Unit)? = null,
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = Color.White,
    focusedBorderWidth: Dp = 2.dp,
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    labelColorFocused: Color = MaterialTheme.colorScheme.primary,
    labelColorUnfocused: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    supportingText: String? = null,
    paddingValues: PaddingValues = PaddingValues(start =  0.dp, end = 8.dp, top = 0.dp, bottom = 0.dp),
    errorColor: Color = MaterialTheme.colorScheme.error
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                focusRequester.requestFocus()
            }
        }
    }

    val borderColor = when {
        isError -> errorColor
        isFocused -> focusedBorderColor
        else -> unfocusedBorderColor
    }

    val labelColor = when {
        isError -> errorColor
        isFocused -> labelColorFocused
        else -> labelColorUnfocused
    }

    val borderStroke = BorderStroke(
        width = if (isFocused) focusedBorderWidth else borderWidth,
        color = borderColor
    )

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    focusRequester.requestFocus()
                }
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                border = borderStroke,
                color = backgroundColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    prefix?.let {
                        Box(modifier = Modifier.padding(start = 8.dp)) {
                            it()
                        }
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = textStyle,
                        singleLine = true,
                        keyboardOptions = keyboardOptions,
                        modifier = Modifier
                            .weight(1f)
                            .padding(paddingValues)
                            .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                            .focusRequester(focusRequester),
                        visualTransformation = visualTransformation,
                        cursorBrush = SolidColor(if (isError) errorColor else focusedBorderColor)
                    )
                }
            }

            // Floating Label
            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .offset(y = (-8).dp)
                    .background(backgroundColor)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = labelColor
                )
            }
        }

        Text(
            text = supportingText ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) errorColor else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
