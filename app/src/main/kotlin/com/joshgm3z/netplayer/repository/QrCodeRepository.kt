package com.joshgm3z.netplayer.repository

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.joshgm3z.netplayer.util.Logger
import com.journeyapps.barcodescanner.BarcodeEncoder
import javax.inject.Inject

const val ONLINE_INPUT_URL = "https://net-player-487fb.web.app"

class QrCodeRepository
@Inject constructor(
    private val firestoreWrapper: FirestoreWrapper,
) {
    fun getQrCodeBitmap(sessionId: String): Bitmap? {
        val url = "$ONLINE_INPUT_URL?id=$sessionId"
        return url.getQrCode()
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