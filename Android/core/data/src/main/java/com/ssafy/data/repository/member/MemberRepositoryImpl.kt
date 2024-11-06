package com.ssafy.data.repository.member

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toEntity
import com.ssafy.database.dao.MemberBackgroundDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dto.MemberBackgroundEntity
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.model.background.BackgroundDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.user.User
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.network.source.member.MemberDataSource
import com.ssafy.network.source.member.MemberPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepositoryImpl @Inject constructor(
    private val memberDataSource: MemberDataSource,
    private val memberDao: MemberDao,
    private val memberBackgroundDao: MemberBackgroundDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MemberRepository {

    override suspend fun getMember(memberId: Long): Flow<User> = withContext(ioDispatcher) {
        memberDao.getMember(memberId)
            .map { it.toDTO() }
    }

    override suspend fun updateMember(
        memberUpdateRequestDto: MemberUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            if (isConnected) {
                memberDataSource.updateMember(memberUpdateRequestDto)
            }

            // TODO 내 PK 또는 이메일
//            memberDao.updateMember()
        }
    }

    override suspend fun searchMembers(
        keyword: String,
        sort: List<String>
    ): Flow<PagingData<User>> = Pager(
        config = PagingConfig(pageSize = MemberPagingSource.PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = {
            MemberPagingSource(
                memberDataSource = memberDataSource,
                keyword = keyword,
                sort = sort
            )
        }
    ).flow.flowOn(ioDispatcher)

    override suspend fun getLocalCreateMemberBackgrounds(): List<BackgroundDto> =
        withContext(ioDispatcher) {
            memberBackgroundDao.getLocalCreateMemberBackgrounds()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationMemberBackgrounds(): List<BackgroundDto> =
        withContext(ioDispatcher) {
            memberBackgroundDao.getLocalOperationMemberBackgrounds()
                .map { it.toDTO() }
        }

    override suspend fun getMemberBackground(id: Long): BackgroundDto =
        withContext(ioDispatcher) {
            memberBackgroundDao.getMemberBackground(id)
                .toDTO()
        }

    override suspend fun getAllMemberBackgrounds(): Flow<List<BackgroundDto>> =
        withContext(ioDispatcher) {
            memberBackgroundDao.getAllMemberBackgrounds()
                .map { entities -> entities.map { it.toDTO() } }
        }

    override suspend fun createMemberBackground(
        background: BackgroundDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            memberDataSource.createMemberBackground(background)
        } else {
            flow { memberBackgroundDao.insertMemberBackground(background.toEntity()) }
        }
    }

    override suspend fun deleteMemberBackground(id: Long, isConnected: Boolean): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val memberBackground = getMemberBackground(id).toEntity()

            if(memberBackground != null) {
                if (isConnected) {
                    memberDataSource.deleteMemberBackground(id)
                } else {
                    when(memberBackground.isStatus) {
                        DataStatus.CREATE ->
                            memberBackgroundDao.deleteMemberBackground(memberBackground)
                        else ->
                            memberBackgroundDao.updateMemberBackground(id, DataStatus.DELETE.name)
                    }
                }
            }
        }
    }

}
