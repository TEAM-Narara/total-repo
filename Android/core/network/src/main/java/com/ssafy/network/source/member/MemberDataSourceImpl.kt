package com.ssafy.network.source.member

import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.user.User
import com.ssafy.network.api.MemberAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class MemberDataSourceImpl @Inject constructor(
    private val memberAPI: MemberAPI
) : MemberDataSource {

    override suspend fun getMembers(): Response<ApiResponse<User>> =
        memberAPI.getMembers()

    override suspend fun updateMember(memberUpdateRequestDto: MemberUpdateRequestDto): Response<ApiResponse<Unit>> =
        memberAPI.updateMember(memberUpdateRequestDto)

    override suspend fun searchMembers(
        keyword: String,
        pageDto: PageDto
    ): Response<ApiResponse<List<User>>> = memberAPI.searchMembers(keyword, pageDto)

}
