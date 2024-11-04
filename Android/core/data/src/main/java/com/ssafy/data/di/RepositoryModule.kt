package com.ssafy.data.di

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.board.BoardRepositoryImpl
import com.ssafy.data.repository.github.GitHubRepository
import com.ssafy.data.repository.github.GitHubRepositoryImpl
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.data.repository.member.MemberRepositoryImpl
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

    @Singleton
    @Binds
    abstract fun bindBoardRepository(boardRepositoryImpl: BoardRepositoryImpl): BoardRepository

    @Singleton
    @Binds
    abstract fun bindMemberRepository(memberRepositoryImpl: MemberRepositoryImpl): MemberRepository

}
