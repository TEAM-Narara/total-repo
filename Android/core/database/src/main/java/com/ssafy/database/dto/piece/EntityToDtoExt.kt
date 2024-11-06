package com.ssafy.database.dto.piece

import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.model.workspace.WorkSpaceDTO

fun WorkspaceEntity.toDTO(): WorkSpaceDTO {
    return WorkSpaceDTO(
        workSpaceId = this.id,
        authority = this.authority,
        name = this.name,
        isStatus = this.isStatus
    )
}