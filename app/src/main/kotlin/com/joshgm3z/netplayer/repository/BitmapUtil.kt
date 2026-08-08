package com.joshgm3z.netplayer.repository

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.joshgm3z.netplayer.util.Logger
import com.journeyapps.barcodescanner.BarcodeEncoder

fun getBitmap(
    text: String,
    width: Int = 512,
    height: Int = 512,
): Bitmap? = try {
    Logger.debug("textToEncode = [$text]")
    BarcodeEncoder().encodeBitmap(
        text,
        BarcodeFormat.QR_CODE,
        width, height
    )
} catch (e: Exception) {
    Logger.error(e.message.toString())
    e.printStackTrace()
    null
}
