package com.ssafy.network.source.member

import android.graphics.BitmapFactory
import com.ssafy.model.background.CoverDto
import com.ssafy.model.member.MemberBackgroundDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.member.SearchMemberResponse
import com.ssafy.model.user.User
import com.ssafy.network.api.MemberAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import com.ssafy.network.util.S3ImageUtil
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.UUID
import javax.inject.Inject

class MemberDataSourceImpl @Inject constructor(
    private val memberAPI: MemberAPI,
    private val s3ImageUtil: S3ImageUtil
) : MemberDataSource {

    override suspend fun getMembers(): Flow<User> =
        safeApiCall { memberAPI.getMembers() }.toFlow()

    override suspend fun updateMember(
        memberId: Long,
        memberUpdateRequestDto: MemberUpdateRequestDto
    ): Flow<Unit> {
        val key = "${memberId}/profile"

        memberUpdateRequestDto.profileImgUrl?.let { url ->
            uploadS3Image(url, key)
        }

        val newMemberRequestDto = if (!memberUpdateRequestDto.profileImgUrl.isNullOrBlank()) {
            memberUpdateRequestDto.copy(profileImgUrl = key)
        } else {
            memberUpdateRequestDto
        }

        return safeApiCall {
            memberAPI.updateMember(
                newMemberRequestDto.nickname,
                newMemberRequestDto.profileImgUrl ?: ""
            )
        }.toFlow()
    }

    override suspend fun searchMembers(
        keyword: String,
        pageDto: PageDto
    ): Flow<SearchMemberResponse> =
        safeApiCall { memberAPI.searchMembers(keyword, pageDto) }.toFlow()

    override suspend fun createMemberBackground(
        memberId: Long,
        background: CoverDto
    ): Flow<MemberBackgroundDto> {
        val key = "${memberId}/background/${UUID.randomUUID()}"
        if (background.imgPath.isNotBlank()) {
            uploadS3Image(background.imgPath, key)
        }
        return safeApiCall { memberAPI.createMemberBackground(memberId, key) }.toFlow()
    }

    override suspend fun deleteMemberBackground(memberId: Long, backgroundId: Long): Flow<Unit> {
        return safeApiCall { memberAPI.deleteMemberBackground(memberId, backgroundId) }.toFlow()
    }

    private suspend fun uploadS3Image(url: String, key: String) {
        val file = File(url)

        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.path)
            val resizedBitmap = s3ImageUtil.rescaleBitmap(bitmap)
            val rescaledFile = s3ImageUtil.bitmapToFile(resizedBitmap)
            val rescaledKey = "${key}-${S3ImageUtil.MINI}"

            s3ImageUtil.uploadFile(
                key = rescaledKey,
                file = rescaledFile,
                isImage = true
            )

            s3ImageUtil.uploadFile(
                key = key,
                file = file,
                isImage = true
            )
        }
    }
}
