package com.ssafy.network.module

import com.ssafy.network.api.GitHubAPI
import com.ssafy.network.api.UserAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideUserAPI(@UserRetrofit retrofit: Retrofit): UserAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideGitHubAPI(@GitHubRetrofit retrofit: Retrofit): GitHubAPI = retrofit.create()

}
