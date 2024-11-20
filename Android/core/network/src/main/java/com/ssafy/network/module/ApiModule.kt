package com.ssafy.network.module

import com.ssafy.network.api.AuthAPI
import com.ssafy.network.api.BoardAPI
import com.ssafy.network.api.CardAPI
import com.ssafy.network.api.CardLabelAPI
import com.ssafy.network.api.CommentAPI
import com.ssafy.network.api.FcmAPI
import com.ssafy.network.api.GitHubAPI
import com.ssafy.network.api.KafkaAPI
import com.ssafy.network.api.LabelAPI
import com.ssafy.network.api.ListAPI
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

    @Singleton
    @Provides
    fun provideCommentAPI(@UserRetrofit retrofit: Retrofit): CommentAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideCardAPI(@UserRetrofit retrofit: Retrofit): CardAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideListAPI(@UserRetrofit retrofit: Retrofit): ListAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideLabelAPI(@UserRetrofit retrofit: Retrofit): LabelAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideCardLabelAPI(@UserRetrofit retrofit: Retrofit): CardLabelAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideKafkaAPI(@UserRetrofit retrofit: Retrofit): KafkaAPI = retrofit.create()

    @Singleton
    @Provides
    fun provideFcmAPI(@UserRetrofit retrofit: Retrofit): FcmAPI = retrofit.create()

}
