package com.ssafy.model.user

sealed class OAuth {
    data class GitHub(val token: String) : OAuth()
    data class Naver(val token: String) : OAuth()
}
