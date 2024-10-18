package com.ssafy.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIME_OUT = 25000L

    @Singleton
    @Provides
    fun provideOkHttpClient(
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
        .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
        .build()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .create()

    @Singleton
    @Provides
    fun provideUserRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

}
