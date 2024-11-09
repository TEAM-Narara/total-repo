package com.ssafy.database.di

import android.content.Context
import androidx.room.Room
import com.ssafy.database.dao.AlertDao
import com.ssafy.database.dao.AttachmentDao
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.BoardMemberDao
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.CardLabelDao
import com.ssafy.database.dao.CardMemberDao
import com.ssafy.database.dao.LabelDao
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dao.ListMemberDao
import com.ssafy.database.dao.LocalKeyDao
import com.ssafy.database.dao.MemberBackgroundDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dao.ReplyDao
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.database.SBDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DBModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SBDatabase = Room
        .databaseBuilder(context, SBDatabase::class.java, "sb_database.db")
        .build()

    @Singleton
    @Provides
    fun provideAlertDao(appDatabase: SBDatabase): AlertDao = appDatabase.AlertDao()

    @Singleton
    @Provides
    fun provideAttachmentDao(appDatabase: SBDatabase): AttachmentDao = appDatabase.AttachmentDao()

    @Singleton
    @Provides
    fun provideBoardDao(appDatabase: SBDatabase): BoardDao = appDatabase.BoardDao()

    @Singleton
    @Provides
    fun provideBoardMemberDao(appDatabase: SBDatabase): BoardMemberDao = appDatabase.BoardMemberDao()

    @Singleton
    @Provides
    fun provideCardDao(appDatabase: SBDatabase): CardDao = appDatabase.CardDao()

    @Singleton
    @Provides
    fun provideCardLabelDao(appDatabase: SBDatabase): CardLabelDao = appDatabase.CardLabelDao()

    @Singleton
    @Provides
    fun provideCardMemberDao(appDatabase: SBDatabase): CardMemberDao = appDatabase.CardMemberDao()

    @Singleton
    @Provides
    fun provideLabelDao(appDatabase: SBDatabase): LabelDao = appDatabase.LabelDao()

    @Singleton
    @Provides
    fun provideListDao(appDatabase: SBDatabase): ListDao = appDatabase.ListDao()

    @Singleton
    @Provides
    fun provideListMemberDao(appDatabase: SBDatabase): ListMemberDao = appDatabase.ListMemberDao()

    @Singleton
    @Provides
    fun provideMemberBackgroundDao(appDatabase: SBDatabase): MemberBackgroundDao = appDatabase.MemberBackgroundDao()

    @Singleton
    @Provides
    fun provideMemberDao(appDatabase: SBDatabase): MemberDao = appDatabase.MemberDao()

    @Singleton
    @Provides
    fun provideReplyDao(appDatabase: SBDatabase): ReplyDao = appDatabase.ReplyDao()

    @Singleton
    @Provides
    fun provideWorkspaceDao(appDatabase: SBDatabase): WorkspaceDao = appDatabase.WorkspaceDao()

    @Singleton
    @Provides
    fun provideWorkspaceMemberDao(appDatabase: SBDatabase): WorkspaceMemberDao = appDatabase.WorkspaceMemberDao()

    @Singleton
    @Provides
    fun provideLocalKeyDao(appDatabase: SBDatabase): LocalKeyDao = appDatabase.LocalKeyDao()
}