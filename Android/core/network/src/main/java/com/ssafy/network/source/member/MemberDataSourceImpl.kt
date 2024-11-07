package com.ssafy.network.source.member

import com.ssafy.model.background.CoverDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.user.User
import com.ssafy.network.api.MemberAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemberDataSourceImpl @Inject constructor(
    private val memberAPI: MemberAPI
) : MemberDataSource {

    override suspend fun getMembers(): Flow<User> =
        safeApiCall { memberAPI.getMembers() }.toFlow()

    override suspend fun updateMember(memberUpdateRequestDto: MemberUpdateRequestDto): Flow<Unit> =
        safeApiCall { memberAPI.updateMember(memberUpdateRequestDto) }.toFlow()

    override suspend fun searchMembers(
        keyword: String,
        pageDto: PageDto
    ): Flow<List<User>> = safeApiCall { memberAPI.searchMembers(keyword, pageDto) }.toFlow()

    override suspend fun createMemberBackground(background: CoverDto): Flow<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMemberBackground(id: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

}
