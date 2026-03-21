package com.example.wavecast.core.network.api

import com.example.wavecast.core.network.model.PodcastSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PodcastIndexApi {
    @GET("search/byterm")
    suspend fun searchPodcasts(
        @Query("q") term: String,
        @Query("max") max: Int = 20
    ): PodcastSearchResponse

    @GET("podcasts/trending")
    suspend fun getTrendingPodcasts(
        @Query("max") max: Int = 20
    ): PodcastSearchResponse
}
