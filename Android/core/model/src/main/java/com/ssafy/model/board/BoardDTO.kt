package com.ssafy.model.board

import com.ssafy.model.with.DataStatus

data class BoardDTO(
    val id: Long,
    val workspaceId: Long,
    val name: String? = null,
    val backgroundType: BackgroundType? = null,
    val backgroundValue: String? = null,
    val isClosed: Boolean? = null,
    val visibility: String? = null,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

enum class Visibility {
    WORKSPACE,
    PRIVATE;
}
enum class BackgroundType {
    COLOR,
    IMAGE;
}
