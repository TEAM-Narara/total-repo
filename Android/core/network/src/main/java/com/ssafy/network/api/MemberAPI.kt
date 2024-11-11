package com.ssafy.network.api

import com.ssafy.model.member.PageDto
import com.ssafy.model.member.SearchMemberResponse
import com.ssafy.model.user.User
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface MemberAPI {

    @GET("api/v1/members")
    suspend fun getMembers(): Response<ApiResponse<User>>

    @PATCH("api/v1/members")
    suspend fun updateMember(
        @Query("nickname") nickname: String,
        @Query("profileImgUrl") profileImgUrl: String
    ): Response<ApiResponse<Unit>>

    @GET("api/v1/members/search")
    suspend fun searchMembers(
        @Query("searchTerm") keyword: String,
        @Query("pageable") pageDto: PageDto
    ): Response<ApiResponse<SearchMemberResponse>>

}
