package com.ssafy.model.board

import kotlinx.serialization.Serializable

data class BoardDTO(
    val id: Long,
    val workspaceId: Long,
    val name: String,
    val background: Background,
    val isClosed: Boolean,
    val visibility: Visibility,
)

@Serializable
data class Background(
    val type: BackgroundType,
    val value: String,
)

enum class BackgroundType {
    COLOR,
    IMAGE;
}

enum class Visibility {
    WORKSPACE,
    PRIVATE;
}
