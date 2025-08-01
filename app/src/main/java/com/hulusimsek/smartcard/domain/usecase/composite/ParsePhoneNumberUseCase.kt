package com.hulusimsek.smartcard.domain.usecase.composite

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
class ParsePhoneNumberUseCase {

    operator fun invoke(rawNumber: String): Triple<String?, String?, String?> {
        val cleaned = rawNumber.replace(Regex("[+-]"), "")
        val isValidFormat = cleaned.matches(Regex("^\\d+$"))
        if (!isValidFormat) {
            return Triple(null, null, null)
        }


        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            // Eksiği: Kullanıcının doğrudan "551112233" gibi girdiğini varsayalım
            val parsedNumber: PhoneNumber = phoneUtil.parse("+$rawNumber", null)

            // Yeni: Geçerli numara mı kontrolü
            if (!phoneUtil.isPossibleNumber(parsedNumber) || !phoneUtil.isValidNumber(parsedNumber)) {
                return Triple(null, null, null)
            }

            val countryCode = "+${parsedNumber.countryCode}"
            val regionCode = phoneUtil.getRegionCodeForNumber(parsedNumber)
            val nationalNumber = parsedNumber.nationalNumber.toString()

            val areaCodeLength = phoneUtil.getLengthOfGeographicalAreaCode(parsedNumber)
            val areaCode = nationalNumber.take(areaCodeLength)
            val subscriberNumber = nationalNumber.drop(areaCodeLength)

            Triple(countryCode, areaCode, subscriberNumber)
        } catch (e: NumberParseException) {
            e.printStackTrace()
            Triple(null, null, null)
        }
    }
}