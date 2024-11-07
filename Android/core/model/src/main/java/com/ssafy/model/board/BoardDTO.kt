package com.ssafy.model.board

data class BoardDTO(
    val id: Long,
    val workspaceId: Long,
    val name: String,
    val background: Background,
    val isClosed: Boolean,
    val visibility: Visibility,
)

enum class Visibility {
    WORKSPACE,
    PRIVATE;
}
