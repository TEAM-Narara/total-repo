package com.ssafy.model.board

import com.ssafy.model.background.Background
import com.ssafy.model.with.DataStatus

data class BoardDTO(
    val id: Long,
    val workspaceId: Long,
    val name: String,
    val background: Background,
    val isClosed: Boolean,
    val visibility: Visibility,

    @Transient
    val isStatus: DataStatus? = DataStatus.STAY
)

enum class Visibility(name: String) {
    WORKSPACE("WORKSPACE"),
    PRIVATE("PRIVATE")
}
