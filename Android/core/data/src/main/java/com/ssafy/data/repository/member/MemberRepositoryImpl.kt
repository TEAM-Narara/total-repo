package com.ssafy.data.repository.member

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ssafy.data.di.IoDispatcher
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.user.User
import com.ssafy.network.source.member.MemberDataSource
import com.ssafy.network.source.member.MemberPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepositoryImpl @Inject constructor(
    private val memberDataSource: MemberDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MemberRepository {

    override suspend fun getMember(): Flow<User> = withContext(ioDispatcher) {
        TODO("Room DB에서 User 정보를 가져옵니다.")
    }

    override suspend fun updateMember(
        memberUpdateRequestDto: MemberUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            memberDataSource.updateMember(memberUpdateRequestDto).map {
                TODO("Room DB에 User 정보를 업데이트합니다.")
            }
        } else {
            TODO("Room DB에 User 정보를 업데이트합니다.")
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

}
