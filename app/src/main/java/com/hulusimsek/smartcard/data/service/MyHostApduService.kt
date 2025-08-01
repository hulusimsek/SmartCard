package com.hulusimsek.smartcard.data.service

import android.app.LocaleConfig.STATUS_SUCCESS
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import javax.inject.Singleton


class MyHostApduService : HostApduService() {

    companion object {
        var isServiceActive = false
        var jsonToSend: String = ""
        private val SELECT_APDU_HEADER = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(), 0x07.toByte(),
            0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x02.toByte(), 0x47.toByte(), 0x10.toByte(), 0x01.toByte()
        )
        private val STATUS_SUCCESS = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val STATUS_FILE_NOT_FOUND = byteArrayOf(0x6A.toByte(), 0x82.toByte())
        private val STATUS_SERVICE_DISABLED = byteArrayOf(0x6F.toByte(), 0x00.toByte())
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        Log.d("nfcdeneme", "processCommandApdu called: ${commandApdu?.joinToString(" ") { "%02X".format(it) }}")

        if (!isServiceActive || jsonToSend.isEmpty()) {
            Log.d("nfcdeneme", "Service inactive or no data to send")
            return STATUS_SERVICE_DISABLED
        }

        if (commandApdu != null && commandApdu.size >= SELECT_APDU_HEADER.size) {
            val receivedHeader = commandApdu.take(SELECT_APDU_HEADER.size).toByteArray()
            if (receivedHeader.contentEquals(SELECT_APDU_HEADER)) {
                Log.d("nfcdeneme", "AID matched. Sending data: $jsonToSend")
                val data = jsonToSend.toByteArray(Charsets.UTF_8)
                return data + STATUS_SUCCESS
            }
        }

        Log.d("nfcdeneme", "Invalid command received")
        return STATUS_FILE_NOT_FOUND
    }

    override fun onDeactivated(reason: Int) {
        Log.d("nfcdeneme", "Service deactivated: $reason")
    }
}