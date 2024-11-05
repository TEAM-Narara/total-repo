package com.ssafy.network.source.member

import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.user.User
import com.ssafy.network.source.ApiResponse
import retrofit2.Response

interface MemberDataSource {

    suspend fun getMembers(): Response<ApiResponse<User>>

    suspend fun updateMember(memberUpdateRequestDto: MemberUpdateRequestDto): Response<ApiResponse<Unit>>

    suspend fun searchMembers(keyword: String, pageDto: PageDto): Response<ApiResponse<List<User>>>

}
