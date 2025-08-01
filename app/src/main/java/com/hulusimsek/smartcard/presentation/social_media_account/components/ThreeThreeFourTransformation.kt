package com.hulusimsek.smartcard.presentation.social_media_account.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ThreeThreeFourTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text
        val builder = StringBuilder()

        for (i in trimmed.indices) {
            builder.append(trimmed[i])
            if (i == 2 || i == 5) builder.append(" ")
        }

        val formatted = builder.toString()

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var spaces = 0
                if (offset > 2) spaces++
                if (offset > 5) spaces++
                return offset + spaces
            }

            override fun transformedToOriginal(offset: Int): Int {
                var spaces = 0
                if (offset > 3) spaces++
                if (offset > 7) spaces++
                return offset - spaces
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetTranslator)
    }
}
