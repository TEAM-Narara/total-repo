package com.ssafy.data.repository

import com.ssafy.database.dto.MemberBackgroundEntity
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.model.background.BackgroundDto
import com.ssafy.model.workspace.WorkSpaceDTO

fun WorkSpaceDTO.toEntity(): WorkspaceEntity {
    return WorkspaceEntity(
        id = this.workSpaceId,
        name = this.name,
        authority = this.authority,
        isStatus = this.isStatus
    )
}

fun BackgroundDto.toEntity(): MemberBackgroundEntity {
    return MemberBackgroundEntity(
        id = this.id,
        url = this.imgPath,
        isStatus = this.isStatus
    )
}