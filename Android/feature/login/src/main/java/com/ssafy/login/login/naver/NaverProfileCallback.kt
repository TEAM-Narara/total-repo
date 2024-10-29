package com.ssafy.login.login.naver

import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.ssafy.model.user.User

class NaverProfileCallback(
    private val onSuccess: (User) -> Unit,
    private val onFailure: (message: String) -> Unit
) : NidProfileCallback<NidProfileResponse> {

    override fun onError(errorCode: Int, message: String) {
        onFailure(errorCode, message)
    }

    override fun onFailure(httpStatus: Int, message: String) {
        onFailure(message)
    }

    override fun onSuccess(result: NidProfileResponse) {
        val email = result.profile?.email ?: return onFailure("이메일을 가져올 수 없습니다.")
        val nickname = result.profile?.nickname ?: return onFailure("닉네임을 가져올 수 없습니다.")
        val profileImage = result.profile?.profileImage
        val user = User(email = email, nickname = nickname, profileImage = profileImage)
        onSuccess(user)
    }
}
