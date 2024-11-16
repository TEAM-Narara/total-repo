package com.ssafy.model.user.github

import com.google.gson.annotations.SerializedName

data class GitHubDTO(
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("code")
    val code: String
)
