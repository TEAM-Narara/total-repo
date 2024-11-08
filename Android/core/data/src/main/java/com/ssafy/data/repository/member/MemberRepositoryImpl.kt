package com.ssafy.data.repository.member

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toEntity
import com.ssafy.database.dao.MemberBackgroundDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.model.background.CoverDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.user.User
import com.ssafy.model.with.DataStatus
import com.ssafy.network.source.member.MemberDataSource
import com.ssafy.network.source.member.MemberPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

    override suspend fun getMember(memberId: Long): Flow<User?> =
        withContext(ioDispatcher) {
        memberDao.getMemberFlow(memberId)
            .map { it?.toDTO() }
    }

    override suspend fun updateMember(
        memberId: Long,
        memberUpdateRequestDto: MemberUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val myInfo = memberDao.getMember(memberId)

            if(myInfo != null) {
                if (isConnected) {
                    memberDataSource.updateMember(memberUpdateRequestDto)
                } else {
                    // TODO 서버에 동기화 isStatus
                    val result = memberDao.updateMember(myInfo.copy(
                        nickname = memberUpdateRequestDto.nickname,
                        profileImageUrl = memberUpdateRequestDto.profileImgUrl,
                    ))

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun searchMembers(
        keyword: String,
        sort: List<String>,
        filterList: List<Long>,
    ): Flow<PagingData<User>> = Pager(
        config = PagingConfig(pageSize = MemberPagingSource.PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = {
            MemberPagingSource(
                memberDataSource = memberDataSource,
                keyword = keyword,
                sort = sort,
                filterList = filterList
            )
        }
    ).flow.flowOn(ioDispatcher)

    override suspend fun getLocalCreateMemberBackgrounds(): List<CoverDto> =
        withContext(ioDispatcher) {
            memberBackgroundDao.getLocalCreateMemberBackgrounds()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationMemberBackgrounds(): List<CoverDto> =
        withContext(ioDispatcher) {
            memberBackgroundDao.getLocalOperationMemberBackgrounds()
                .map { it.toDTO() }
        }

    override suspend fun getMemberBackground(id: Long): CoverDto? =
        withContext(ioDispatcher) {
            memberBackgroundDao.getMemberBackground(id)
                ?.toDTO()
        }

    override suspend fun getAllMemberBackgrounds(): Flow<List<CoverDto>> =
        withContext(ioDispatcher) {
            memberBackgroundDao.getAllMemberBackgrounds()
                .map { entities -> entities.map { it.toDTO() } }
        }

    override suspend fun createMemberBackground(
        background: CoverDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            memberDataSource.createMemberBackground(background)
        } else {
            flowOf(memberBackgroundDao.insertMemberBackground(background.toEntity()))
        }
    }

    override suspend fun deleteMemberBackground(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val memberBackground = getMemberBackground(id)

            if(memberBackground != null) {
                if (isConnected) {
                    memberDataSource.deleteMemberBackground(id)
                } else {
                    val result = when(memberBackground.isStatus) {
                        DataStatus.CREATE ->
                            memberBackgroundDao.deleteMemberBackground(memberBackground.toEntity())
                        else ->
                            memberBackgroundDao.updateMemberBackground(id, DataStatus.DELETE.name)
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

}
