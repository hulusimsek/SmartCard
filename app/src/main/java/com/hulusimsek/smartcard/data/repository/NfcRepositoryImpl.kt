package com.hulusimsek.smartcard.data.repository

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import com.hulusimsek.smartcard.data.service.MyHostApduService
import com.hulusimsek.smartcard.domain.model.NfcData
import com.hulusimsek.smartcard.domain.model.NfcMode
import com.hulusimsek.smartcard.domain.provider.ActivityProvider
import com.hulusimsek.smartcard.domain.repository.NfcRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NfcRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityProvider: ActivityProvider
) : NfcRepository {

    private var nfcAdapter: NfcAdapter? = null
    private var currentMode = NfcMode.LISTENER

    private val _nfcDataFlow = MutableSharedFlow<NfcData>()

    init {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    }

    override suspend fun enableListenerMode(): Result<Unit> = withContext(Dispatchers.Main) {
            try {
                currentMode = NfcMode.LISTENER
                MyHostApduService.isServiceActive = false

                val activity = activityProvider.getCurrentActivity()
                    ?: return@withContext Result.failure(Exception("No current activity available"))

                val options = Bundle().apply {
                    putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)
                }

                nfcAdapter?.enableReaderMode(
                    activity as Activity?,
                    { tag -> handleTagDetected(tag) },
                    NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    options
                )

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }


    override suspend fun enableSenderMode(jsonData: String): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            currentMode = NfcMode.SENDER

            activityProvider.getCurrentActivity()?.let { activity ->
                nfcAdapter?.disableReaderMode(activity as Activity?)
            }

            MyHostApduService.isServiceActive = true
            MyHostApduService.jsonToSend = jsonData

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disableNfcAll(): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            MyHostApduService.isServiceActive = false
            activityProvider.getCurrentActivity()?.let { activity ->
                nfcAdapter?.disableReaderMode(activity as Activity?)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun onResume() {
        when (currentMode) {
            NfcMode.LISTENER -> enableListenerMode()
            NfcMode.SENDER -> enableSenderMode(MyHostApduService.jsonToSend)
            NfcMode.DISABLE -> disableNfcAll()
        }
    }

    override suspend fun onPause() {
        disableNfcAll()
    }

    override suspend fun isNfcEnabled(): Boolean = nfcAdapter?.isEnabled == true
    override suspend fun isNfcAvailable(): Boolean = nfcAdapter != null


    private fun handleTagDetected(tag: Tag) {
        // Your existing tag handling logic
        val isoDep = IsoDep.get(tag) ?: return

        try {
            if (!isoDep.isConnected) {
                isoDep.connect()
            }

            val command = byteArrayOf(
                0x00.toByte(),
                0xA4.toByte(),
                0x04.toByte(),
                0x00.toByte(),
                0x07.toByte(),
                0xA0.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x02.toByte(),
                0x47.toByte(),
                0x10.toByte(),
                0x01.toByte()
            )

            val response = isoDep.transceive(command)
            val responseData =
                if (response.size > 2) response.copyOfRange(0, response.size - 2) else byteArrayOf()
            val responseStr = responseData.toString(Charsets.UTF_8)

            val nfcData = NfcData(responseStr)

            CoroutineScope(Dispatchers.Default).launch {
                _nfcDataFlow.emit(nfcData)
            }

            isoDep.close()
        } catch (e: IOException) {
            Log.e("NfcRepository", "IOException: ${e.message}")
        }
    }


    override val nfcDataFlow: SharedFlow<NfcData>
        get() = _nfcDataFlow // Buraya ekle
}