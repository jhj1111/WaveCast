package com.example.wavecast.core.network.api

import com.example.wavecast.core.network.model.EpisodeResponse
import com.example.wavecast.core.network.utils.RssParser
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RssService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val rssParser: RssParser
) {
    suspend fun fetchEpisodes(url: String): List<EpisodeResponse> {
        val request = Request.Builder()
            .url(url)
            .build()
        
        val response = okHttpClient.newCall(request).execute()
        return response.body?.byteStream()?.use { inputStream ->
            rssParser.parse(inputStream)
        } ?: emptyList()
    }
}
