package com.hulusimsek.smartcard.presentation.common.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.DimenRes

fun Context.getFloatDimen(@DimenRes dimenResId: Int): Float {
    val typedValue = TypedValue()
    resources.getValue(dimenResId, typedValue, true)
    return typedValue.float
}