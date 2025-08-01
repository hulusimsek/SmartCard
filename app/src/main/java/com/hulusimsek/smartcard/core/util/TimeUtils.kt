package com.hulusimsek.smartcard.core.util

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun getCurrentDateTimeString(): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }

    fun formatDateForDisplay(rawDate: String): String {
        if(rawDate.isNullOrEmpty()) {
            return ""
        }
        return try {
            val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(rawDate) ?: return rawDate

            val outputFormat = SimpleDateFormat("dd MMM yyyy, EEEE • HH:mm", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            rawDate // hata olursa ham veriyi göster
        }
    }
}