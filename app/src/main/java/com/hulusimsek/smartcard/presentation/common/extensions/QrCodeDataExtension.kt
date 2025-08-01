package com.hulusimsek.smartcard.presentation.common.extensions

import android.graphics.Bitmap
import com.hulusimsek.smartcard.domain.model.QrCodeData
import androidx.core.graphics.createBitmap

fun QrCodeData.toBitmap(): Bitmap {
    val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}