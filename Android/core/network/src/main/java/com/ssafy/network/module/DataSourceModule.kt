package com.ssafy.network.module

import com.ssafy.network.source.auth.AuthDataSource
import com.ssafy.network.source.auth.AuthDataSourceImpl
import com.ssafy.network.source.board.BoardDataSource
import com.ssafy.network.source.board.BoardDataSourceImpl
import com.ssafy.network.source.card.CardDataSource
import com.ssafy.network.source.card.CardDataSourceImpl
import com.ssafy.network.source.comment.CommentDataSource
import com.ssafy.network.source.comment.CommentDataSourceImpl
import com.ssafy.network.source.fcm.FcmDataSource
import com.ssafy.network.source.fcm.FcmDataSourceImpl
import com.ssafy.network.source.github.GitHubDataSource
import com.ssafy.network.source.github.GitHubDataSourceImpl
import com.ssafy.network.source.list.ListDataSource
import com.ssafy.network.source.list.ListDataSourceImpl
import com.ssafy.network.source.member.MemberDataSource
import com.ssafy.network.source.member.MemberDataSourceImpl
import com.ssafy.network.source.user.UserDataSource
import com.ssafy.network.source.user.UserDataSourceImpl
import com.ssafy.network.source.workspace.WorkspaceDataSource
import com.ssafy.network.source.workspace.WorkspaceDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindGitHubDataSource(gitHubDataSourceImpl: GitHubDataSourceImpl): GitHubDataSource

    @Singleton
    @Binds
    abstract fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource

    @Singleton
    @Binds
    abstract fun bindAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource

    @Singleton
    @Binds
    abstract fun bindWorkspaceDataSource(workspaceDataSourceImpl: WorkspaceDataSourceImpl): WorkspaceDataSource

    @Singleton
    @Binds
    abstract fun bindBoardDataSource(boardDataSourceImpl: BoardDataSourceImpl): BoardDataSource

    @Singleton
    @Binds
    abstract fun bindMemberDataSource(memberDataSourceImpl: MemberDataSourceImpl): MemberDataSource

    @Singleton
    @Binds
    abstract fun bindCommentDataSource(commentDataSourceImpl: CommentDataSourceImpl): CommentDataSource

    @Singleton
    @Binds
    abstract fun bindCardDataSource(cardDataSourceImpl: CardDataSourceImpl): CardDataSource

    @Singleton
    @Binds
    abstract fun bindListDataSource(listDataSourceImpl: ListDataSourceImpl): ListDataSource


    @Singleton
    @Binds
    abstract fun bindFcmDataSource(fcmDataSourceImpl: FcmDataSourceImpl): FcmDataSource

}
