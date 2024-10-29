package com.ssafy.login.login.naver

import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.model.user.User

class NaverLoginCallback(
    private val onSuccess: (User) -> Unit,
    private val onFailure: (message: String) -> Unit
) : OAuthLoginCallback {

    override fun onError(errorCode: Int, message: String) {
        onFailure(errorCode, message)
    }

    override fun onFailure(httpStatus: Int, message: String) {
        onFailure(message)
    }

    override fun onSuccess() {
        val naverProfileCallback = NaverProfileCallback(onSuccess, onFailure)
        NidOAuthLogin().callProfileApi(naverProfileCallback)
    }
}
