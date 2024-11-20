package com.ssafy.model.user.github

import com.google.gson.annotations.SerializedName

data class GitHubResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("scope")
    val scope: String
)