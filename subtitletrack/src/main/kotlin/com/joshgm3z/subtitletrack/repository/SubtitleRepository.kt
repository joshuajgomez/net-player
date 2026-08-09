package com.joshgm3z.subtitletrack.repository

import com.joshgm3z.subtitletrack.repository.retrofit.OpenSubtitlesDownloadRequest
import com.joshgm3z.subtitletrack.repository.retrofit.OpenSubtitlesService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.openjdk.tools.sjavac.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

data class SubtitleData(
    val title: String,
    val language: String? = null,
    val fileId: Int = 0,
    val downloadCount: Int = 0,
    val url: String? = null,
)

private val defaultUrl = "https://api.opensubtitles.com/api/v1/"

class SubtitleRepository
@Inject
constructor(scope: CoroutineScope) {

    private val baseUrl = defaultUrl

    private val openSubtitlesService: OpenSubtitlesService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenSubtitlesService::class.java)
    }

    private var authToken: String? = null

    init {
        scope.launch {
            try {
                val result = openSubtitlesService.login()
                Log.info("result = [$result]")
                authToken = result.token
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun findSubtitles(query: String): List<SubtitleData> {
        Log.info("findSubtitles: query = [${query}]")
        try {
            val response = openSubtitlesService.searchSubtitles(query = query)
            Log.info("response = [$response]")
            val list = mutableListOf<SubtitleData>()
            response.data.map {
                it.attributes.files?.map { file ->
                    list.add(
                        SubtitleData(
                            title = file.fileName,
                            language = it.attributes.language,
                            fileId = file.fileId,
                            downloadCount = it.attributes.downloadCount,
                        )
                    )
                } ?: emptyList()
            }
            return list
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    suspend fun getSubtitleUrl(fileId: Int): String? {
        val token = authToken ?: return null
        return try {
            val response = openSubtitlesService.download(
                token = "Bearer $token",
                body = OpenSubtitlesDownloadRequest(fileId)
            )
            response.link
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}