package com.ssafy.data.repository.member

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.image.ImageStorage
import com.ssafy.database.dao.MemberBackgroundDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dto.MemberBackgroundEntity
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toEntity
import com.ssafy.model.background.CoverDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.user.User
import com.ssafy.model.with.DataStatus
import com.ssafy.network.source.member.MemberDataSource
import com.ssafy.network.source.member.MemberPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
    private val imageStorage: ImageStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MemberRepository {

    override suspend fun addMember(user: User): Flow<Long> = withContext(ioDispatcher) {
        val userId = user.memberId
        imageStorage.saveAll(user.profileImgUrl) { path ->
            memberDao.insertMember(
                MemberEntity(
                    id = user.memberId,
                    email = user.email,
                    nickname = user.nickname,
                    profileImageUrl = path
                )
            )
        }
        flowOf(userId)
    }

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

            if (myInfo != null) {
                if (isConnected) {
                    memberDataSource.updateMember(memberId, memberUpdateRequestDto).also {
                        memberDao.updateMember(
                            myInfo.copy(
                                nickname = memberUpdateRequestDto.nickname,
                                profileImageUrl = memberUpdateRequestDto.profileImgUrl,
                            )
                        )
                    }
                } else {
                    // TODO 서버에 동기화 isStatus
                    val result = memberDao.updateMember(
                        myInfo.copy(
                            nickname = memberUpdateRequestDto.nickname,
                            profileImageUrl = memberUpdateRequestDto.profileImgUrl,
                        )
                    )

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

    override suspend fun getAllMemberBackgrounds(
        memberId: Long,
        isConnected: Boolean
    ): Flow<List<CoverDto>> = withContext(ioDispatcher) {
        if (isConnected) {
            val prevBackgrounds = memberBackgroundDao.getAllMemberBackgrounds().firstOrNull()
            val memberBackgrounds = memberDataSource.getAllBackgrounds(memberId).firstOrNull()
            memberBackgrounds?.let { backgroundList ->
                backgroundList.map { background ->
                    if (prevBackgrounds?.find { it.id == background.memberBackgroundId } == null) {
                        imageStorage.saveAll(background.imgUrl) { path ->
                            val backgroundEntity = MemberBackgroundEntity(
                                id = background.memberBackgroundId,
                                url = path ?: "",
                            )
                            memberBackgroundDao.insertMemberBackgrounds(listOf(backgroundEntity))
                        }
                    }
                }
            }
        }

        memberBackgroundDao.getAllMemberBackgrounds()
            .map { entities -> entities.map { it.toDTO() } }
    }

    override suspend fun createMemberBackground(
        memberId: Long,
        background: CoverDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            memberDataSource.createMemberBackground(memberId, background)
                .map { it.memberBackgroundId }
        } else {
            flowOf(
                memberBackgroundDao.insertMemberBackground(
                    background.copy(isStatus = DataStatus.CREATE).toEntity()
                )
            )
        }
    }

    override suspend fun deleteMemberBackground(
        memberId: Long,
        backgroundId: Long,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        val memberBackground = getMemberBackground(backgroundId)

        if (memberBackground != null) {
            if (isConnected) {
                memberDataSource.deleteMemberBackground(memberId, backgroundId)
            } else {
                val result = when (memberBackground.isStatus) {
                    DataStatus.CREATE ->
                        memberBackgroundDao.deleteMemberBackground(memberBackground.toEntity())

                    else ->
                        memberBackgroundDao.updateMemberBackground(
                            backgroundId,
                            DataStatus.DELETE.name
                        )
                }

                flowOf(result)
            }
        } else {
            flowOf(Unit)
        }
    }

}
