package com.ssafy.data.repository

import com.ssafy.database.dto.AttachmentEntity
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.LabelEntity
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.MemberBackgroundEntity
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.WorkspaceMemberEntity
import com.ssafy.database.dto.with.CardLabelWithLabelInfo
import com.ssafy.database.dto.with.ReplyWithMemberInfo
import com.ssafy.model.background.CoverDto
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardLabelWithLabelDTO
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ReplyWithMemberDTO
import com.ssafy.model.workspace.WorkSpaceDTO

fun WorkSpaceDTO.toEntity(): WorkspaceEntity {
    return WorkspaceEntity(
        id = this.workSpaceId,
        name = this.name,
        authority = this.authority,
        isStatus = this.isStatus ?: DataStatus.STAY
    )
}

fun BoardDTO.toEntity(): BoardEntity {
    return BoardEntity(
        id = this.id,
        workspaceId = this.workspaceId,
        name = this.name,
        coverType = cover.type.name,
        coverValue = cover.value,
        visibility = visibility.name,
        isClosed = this.isClosed,
        isStatus = DataStatus.STAY,
        columnUpdate = 0L
    )
}

fun LabelDTO.toEntity(): LabelEntity {
    return LabelEntity(
        id = this.id,
        boardId = this.boardId,
        name = this.name,
        color = this.color,
        isStatus = this.isStatus
    )
}

fun CardLabelDTO.toEntity(): CardLabelEntity {
    return CardLabelEntity(
        id = this.id,
        cardId = this.cardId,
        labelId = this.labelId,
        isActivated = this.isActivated,
        isStatus = this.isStatus
    )
}

fun CardLabelWithLabelDTO.toEntity(): CardLabelWithLabelInfo {
    return CardLabelWithLabelInfo(
        cardLabel = CardLabelEntity(
            id = this.cardLabelId,
            labelId = this.labelId,
            cardId = this.cardId,
            isActivated = this.isActivated,
            isStatus = this.cardLabelStatus
        ),
        label = LabelEntity(
            id = this.labelId,
            boardId = this.labelBoardId,
            name = this.labelName,
            color = this.labelColor,
            isStatus = this.labelStatus
        )
    )
}

fun AttachmentDTO.toEntity(): AttachmentEntity {
    return AttachmentEntity(
        id = this.id,
        cardId = this.cardId,
        url = this.url,
        type = this.type,
        isCover = this.isCover,
        isStatus = this.isStatus
    )
}

fun CoverDto.toEntity(): MemberBackgroundEntity {
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

fun ReplyWithMemberInfo.toDto(): ReplyWithMemberDTO {
    return ReplyWithMemberDTO(
        id = this.reply.id,
        cardId = this.reply.cardId,
        memberId = this.reply.memberId,
        content = this.reply.content,
        createAt = this.reply.createAt,
        updateAt = this.reply.updateAt,
        memberEmail = this.member.email,
        memberNickname = this.member.nickname,
        memberProfileImgUrl = this.member.profileImageUrl,
        isStatus = this.reply.isStatus
    )
}

fun SimpleMemberDto.toWorkspaceMemberEntity(workspaceId: Long): WorkspaceMemberEntity {
    return WorkspaceMemberEntity(
        memberId = memberId,
        workspaceId = workspaceId,
        authority = authority,
        isStatus = isStatus ?: DataStatus.STAY
    )
}
