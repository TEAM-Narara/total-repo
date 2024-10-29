package com.ssafy.model.background

import kotlinx.serialization.Serializable

@Serializable
data class BackgroundDto(
    val color: Long,
    val imgPath: String? = null,
)