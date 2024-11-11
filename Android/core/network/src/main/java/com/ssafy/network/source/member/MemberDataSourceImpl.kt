package com.ssafy.network.source.member

import android.graphics.BitmapFactory
import com.ssafy.model.background.CoverDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.member.SearchMemberResponse
import com.ssafy.model.user.User
import com.ssafy.network.api.MemberAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import com.ssafy.network.util.ImageUtil
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class MemberDataSourceImpl @Inject constructor(
    private val memberAPI: MemberAPI,
    private val imageUtil: ImageUtil
) : MemberDataSource {

    override suspend fun getMembers(): Flow<User> =
        safeApiCall { memberAPI.getMembers() }.toFlow()

    override suspend fun updateMember(
        memberId: Long,
        memberUpdateRequestDto: MemberUpdateRequestDto
    ): Flow<Unit> {
        val key = "${memberId}/profile"
        val file = File(memberUpdateRequestDto.profileImgUrl)

        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            val resizedBitmap = imageUtil.rescaleBitmap(bitmap)
            val rescaledFile = imageUtil.bitmapToFile(resizedBitmap)

            imageUtil.uploadFile(
                key = "${key}-mini",
                file = rescaledFile,
                isImage = true
            )

            imageUtil.uploadFile(
                key = key,
                file = file,
                isImage = true
            )
        }

        val newMemberRequestDto = if (memberUpdateRequestDto.profileImgUrl.isNotBlank()) {
            memberUpdateRequestDto.copy(profileImgUrl = key)
        } else {
            memberUpdateRequestDto
        }

        return safeApiCall {
            memberAPI.updateMember(
                newMemberRequestDto.nickname,
                newMemberRequestDto.profileImgUrl
            )
        }.toFlow()
    }

    override suspend fun searchMembers(
        keyword: String,
        pageDto: PageDto
    ): Flow<SearchMemberResponse> =
        safeApiCall { memberAPI.searchMembers(keyword, pageDto) }.toFlow()

    override suspend fun createMemberBackground(background: CoverDto): Flow<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMemberBackground(id: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

}
