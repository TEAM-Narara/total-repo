package com.ssafy.model.board

import kotlinx.serialization.Serializable

@Serializable
data class Background(
    val type: BackgroundType,
    val value: String,
)

enum class BackgroundType {
    COLOR,
    IMAGE;
}