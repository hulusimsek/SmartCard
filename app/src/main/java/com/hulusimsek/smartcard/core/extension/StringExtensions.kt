package com.hulusimsek.smartcard.core.extension

import android.content.Intent
import android.os.Build

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercaseChar() }
}
fun String.clearPlus(): String {
    return this.replace("+", "")
}

fun String.clearTire(): String {
    return this.replace("-", "")
}

fun String.formatAsSmartPhone(): String {
    val digits = this.filter { it.isDigit() }
    val len = digits.length

    return when {
        len <= 3 -> digits
        len <= 6 -> digits.substring(0, 3) + " " + digits.substring(3, len)  // 3 + kalan
        len == 7 -> digits.substring(0, 3) + " " + digits.substring(3, 7)  // 3 + 4
        len in 8..10 -> {
            val part1 = digits.substring(0, 3)
            val part2 = digits.substring(3, 6)
            val part3 = digits.substring(6, len)
            listOf(part1, part2, part3).joinToString(" ")
        }
        else -> digits
    }
}

inline fun <reified T> Intent.getParcelableExtraCompat(name: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(name) as? T
    }
}