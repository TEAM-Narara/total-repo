package com.ssafy.login.login.naver

import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

class NaverLoginCallback(
    private val onSuccess: (String) -> Unit,
    private val onFailure: (String) -> Unit
) : OAuthLoginCallback {

    override fun onError(errorCode: Int, message: String) {
        onFailure(errorCode, message)
    }

    override fun onFailure(httpStatus: Int, message: String) {
        onFailure(message)
    }

    override fun onSuccess() {
        val token = NaverIdLoginSDK.getAccessToken() ?: return onFailure("네이버 데이터 연동 실패!")
        onSuccess(token)
    }
}
