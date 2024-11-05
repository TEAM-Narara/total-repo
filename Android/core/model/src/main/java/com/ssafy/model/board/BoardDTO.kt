package com.ssafy.model.board

data class BoardDTO(
    val id: Long,
    val workspaceId: Long,
    val name: String? = null,
    val backgroundType: String? = null,
    val backgroundValue: String? = null,
    val isClosed: Boolean? = null,
    val visibility: String? = null,
)
