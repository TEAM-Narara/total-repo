package com.ssafy.data.di

import com.ssafy.data.repository.auth.AuthRepository
import com.ssafy.data.repository.auth.AuthRepositoryImpl
import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.board.BoardRepositoryImpl
import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.card.CardRepositoryImpl
import com.ssafy.data.repository.clear.ClearRoomRepository
import com.ssafy.data.repository.clear.ClearRoomRepositoryImpl
import com.ssafy.data.repository.comment.CommentRepository
import com.ssafy.data.repository.comment.CommentRepositoryImpl
import com.ssafy.data.repository.fcm.FcmRepository
import com.ssafy.data.repository.fcm.FcmRepositoryImpl
import com.ssafy.data.repository.github.GitHubRepository
import com.ssafy.data.repository.github.GitHubRepositoryImpl
import com.ssafy.data.repository.list.ListRepository
import com.ssafy.data.repository.list.ListRepositoryImpl
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.data.repository.member.MemberRepositoryImpl
import com.ssafy.data.repository.order.CardMyOrderRepository
import com.ssafy.data.repository.order.CardMyOrderRepositoryImpl
import com.ssafy.data.repository.order.ListMyOrderRepository
import com.ssafy.data.repository.order.ListMyOrderRepositoryImpl
import com.ssafy.data.repository.sync.SyncRepository
import com.ssafy.data.repository.sync.SyncRepositoryImpl
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
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindClearRoomRepository(clearRoomRepositoryImpl: ClearRoomRepositoryImpl): ClearRoomRepository

    @Singleton
    @Binds
    abstract fun bindWorkspaceRepository(workspaceRepositoryImpl: WorkspaceRepositoryImpl): WorkspaceRepository

    @Singleton
    @Binds
    abstract fun bindBoardRepository(boardRepositoryImpl: BoardRepositoryImpl): BoardRepository

    @Singleton
    @Binds
    abstract fun bindMemberRepository(memberRepositoryImpl: MemberRepositoryImpl): MemberRepository

    @Singleton
    @Binds
    abstract fun bindCommentRepository(commentRepositoryImpl: CommentRepositoryImpl): CommentRepository

    @Singleton
    @Binds
    abstract fun bindCardRepository(cardRepositoryImpl: CardRepositoryImpl): CardRepository

    @Singleton
    @Binds
    abstract fun bindListRepository(listRepositoryImpl: ListRepositoryImpl): ListRepository

    @Singleton
    @Binds
    abstract fun bindSyncRepository(syncRepositoryImpl: SyncRepositoryImpl): SyncRepository

    @Singleton
    @Binds
    abstract fun bindListMyOrderRepository(listMyOrderRepository: ListMyOrderRepositoryImpl): ListMyOrderRepository

    @Singleton
    @Binds
    abstract fun bindCardMyOrderRepository(cardMyOrderRepositoryImpl: CardMyOrderRepositoryImpl): CardMyOrderRepository

    @Singleton
    @Binds
    abstract fun bindFcmRepository(fcmRepositoryImpl: FcmRepositoryImpl): FcmRepository
}
