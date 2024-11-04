package com.ssafy.network.module

import com.ssafy.network.source.auth.AuthDataSource
import com.ssafy.network.source.auth.AuthDataSourceImpl
import com.ssafy.network.source.board.BoardDataSource
import com.ssafy.network.source.board.BoardDataSourceImpl
import com.ssafy.network.source.github.GitHubDataSource
import com.ssafy.network.source.github.GitHubDataSourceImpl
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

}
