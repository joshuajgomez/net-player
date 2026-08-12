package com.joshgm3z.netplayer.repository

import android.util.Patterns
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.text.lowercase
import kotlin.text.startsWith

class UrlValidityChecker
@Inject constructor() {

    suspend fun isUrlInvalid(url: String): String? = when {
        !isUrlValid(url) -> "Invalid URL"
        !isMediaExtension(url) -> "File format not supported"
        else -> doesUrlProvideFile(url)
    }

    private suspend fun doesUrlProvideFile(urlString: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode

                // Check if response is successful (200 OK)
                if (responseCode !in 200..299) {
                    return@withContext when (responseCode) {
                        HttpURLConnection.HTTP_NOT_FOUND -> "File not found"
                        HttpURLConnection.HTTP_FORBIDDEN, HttpURLConnection.HTTP_UNAUTHORIZED -> "Access denied"
                        HttpURLConnection.HTTP_BAD_GATEWAY, HttpURLConnection.HTTP_UNAVAILABLE -> "Server unavailable"
                        else -> "Error connecting to server ($responseCode)"
                    }
                }

                // Verify the Content-Type header
                val contentType = connection.contentType ?: ""
                val isMedia = contentType.startsWith("video/") || isMediaExtension(urlString)

                connection.disconnect()
                return@withContext if (isMedia) null else "File format not supported"
            } catch (e: Exception) {
                e.printStackTrace()
                // no internet
                null
            }
        }

    private fun isMediaExtension(url: String): Boolean {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url).lowercase()

        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?.let {
            return it.startsWith("video/")
        }

        val mediaExtensions = listOf(
            "mp3", "mp4", "m4a", "wav", "mkv", "webm", "3gp"
        )
        return mediaExtensions.contains(extension)
    }

    private fun isUrlValid(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }
}