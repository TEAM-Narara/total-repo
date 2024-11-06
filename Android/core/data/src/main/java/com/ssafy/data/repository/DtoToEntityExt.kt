package com.ssafy.data.repository

import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.MemberBackgroundEntity
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.model.background.BackgroundDto
import com.ssafy.model.list.ListResponseDto
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

fun ListResponseDto.toEntity(): ListEntity {
    return ListEntity(
        id = this.listId,
        boardId = this.boardId,
        name = this.name,
        myOrder = this.myOrder,
        isArchived = this.isArchived,
        isStatus = this.isStatus
    )
}