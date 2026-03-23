package com.example.wavecast.core.network.di

import com.example.wavecast.core.network.BuildConfig
import com.example.wavecast.core.network.api.PodcastIndexApi
import com.example.wavecast.core.network.utils.RssParser
import com.example.wavecast.core.network.utils.createHashString
import com.example.wavecast.core.network.utils.getApiHeaderTime
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val PODCAST_INDEX_URL = BuildConfig.PODCAST_INDEX_URL
    private const val PODCAST_API_KEY = BuildConfig.PODCAST_API_KEY
    private const val PODCAST_SECRET_KEY = BuildConfig.PODCAST_SECRET_KEY
    private val apiHeaderTime = getApiHeaderTime()
    private val hashString = createHashString(PODCAST_API_KEY, PODCAST_SECRET_KEY, apiHeaderTime)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Auth-Date", apiHeaderTime)
                .addHeader("X-Auth-Key", PODCAST_API_KEY)
                .addHeader("Authorization", hashString!!)
                .addHeader("User-Agent", "WaveCast/0.0.0 (Android)")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
        }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PODCAST_INDEX_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providePodcastIndexApi(retrofit: Retrofit): PodcastIndexApi {
        return retrofit.create(PodcastIndexApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRssParser(): RssParser = RssParser()
}
