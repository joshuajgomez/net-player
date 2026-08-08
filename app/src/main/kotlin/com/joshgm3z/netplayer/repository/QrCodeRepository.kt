package com.joshgm3z.netplayer.repository

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.joshgm3z.netplayer.util.Logger
import com.journeyapps.barcodescanner.BarcodeEncoder
import javax.inject.Inject

const val ONLINE_INPUT_URL = "https://rocktv-b1cf5.web.app"

class QrCodeRepository
@Inject constructor(
    private val firestoreWrapper: FirestoreWrapper,
    private val dataStoreWrapper: DatastoreWrapper,
) {
    suspend fun getQrCodeBitmap(): Bitmap? {
        val url = "$ONLINE_INPUT_URL?id=${getSessionId()}"
        return url.getQrCode()
    }

    private suspend fun getSessionId() =
        when (val currentSessionId = dataStoreWrapper.getSessionId()) {
            null -> {
                val newId = firestoreWrapper.newDocumentId(
                    "app_session",
                    mapOf("added" to System.currentTimeMillis())
                )
                dataStoreWrapper.setSessionId(newId)
                newId
            }

            else -> currentSessionId
        }

    private fun String.getQrCode(
        width: Int = 512,
        height: Int = 512,
    ): Bitmap? = try {
        Logger.debug("textToEncode = [$this]")
        BarcodeEncoder().encodeBitmap(
            this,
            BarcodeFormat.QR_CODE,
            width, height
        )
    } catch (e: Exception) {
        Logger.error(e.message.toString())
        e.printStackTrace()
        null
    }

}