package com.ssafy.network.module

import com.ssafy.network.api.AuthAPI
import com.ssafy.network.api.BoardAPI
import com.ssafy.network.api.GitHubAPI
import com.ssafy.network.api.MemberAPI
import com.ssafy.network.api.UserAPI
import com.ssafy.network.api.WorkspaceAPI
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
    fun provideGitHubAPI(@GitHubRetrofit retrofit: Retrofit): GitHubAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideAuthAPI(@AuthRetrofit retrofit: Retrofit): AuthAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideUserAPI(@UserRetrofit retrofit: Retrofit): UserAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideWorkspaceAPI(@UserRetrofit retrofit: Retrofit): WorkspaceAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideBoardAPI(@UserRetrofit retrofit: Retrofit): BoardAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideMemberAPI(@UserRetrofit retrofit: Retrofit): MemberAPI = retrofit.create()

}
