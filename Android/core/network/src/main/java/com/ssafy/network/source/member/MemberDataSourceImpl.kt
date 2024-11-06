package com.ssafy.network.source.member

import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.user.User
import com.ssafy.network.api.MemberAPI
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemberDataSourceImpl @Inject constructor(
    private val memberAPI: MemberAPI
) : MemberDataSource {

    override suspend fun getMembers(): Flow<User> =
        memberAPI.getMembers().toFlow()

    override suspend fun updateMember(memberUpdateRequestDto: MemberUpdateRequestDto): Flow<Unit> =
        memberAPI.updateMember(memberUpdateRequestDto).toFlow()

    override suspend fun searchMembers(
        keyword: String,
        pageDto: PageDto
    ): Flow<List<User>> = memberAPI.searchMembers(keyword, pageDto).toFlow()

}