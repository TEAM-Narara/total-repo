package com.ssafy.data.di

import com.ssafy.data.repository.github.GitHubRepository
import com.ssafy.data.repository.github.GitHubRepositoryImpl
import com.ssafy.data.repository.user.UserRepository
import com.ssafy.data.repository.user.UserRepositoryImpl
import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.data.repository.workspace.WorkspaceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindGitHubRepository(gitHubRepositoryImpl: GitHubRepositoryImpl): GitHubRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindWorkspaceRepository(workspaceRepositoryImpl: WorkspaceRepositoryImpl): WorkspaceRepository

}
