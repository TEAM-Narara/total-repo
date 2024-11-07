package com.ssafy.database.dto.piece

import com.ssafy.database.dto.AttachmentEntity
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.CardMemberEntity
import com.ssafy.database.dto.LabelEntity
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.ListMemberAlarmEntity
import com.ssafy.database.dto.ListMemberEntity
import com.ssafy.database.dto.MemberBackgroundEntity
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.ReplyEntity
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.WorkspaceMemberEntity
import com.ssafy.database.dto.with.BoardInList
import com.ssafy.database.dto.with.BoardMemberWithMemberInfo
import com.ssafy.database.dto.with.CardAllInfo
import com.ssafy.database.dto.with.ListInCards
import com.ssafy.database.dto.with.ListMemberWithMemberInfo
import com.ssafy.database.dto.with.ReplyWithMemberInfo
import com.ssafy.database.dto.with.WorkspaceInBoard
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo
import com.ssafy.model.background.Background
import com.ssafy.model.background.BackgroundDto
import com.ssafy.model.background.BackgroundType
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.user.User
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.BoardInListDTO
import com.ssafy.model.with.BoardMemberAlarmDTO
import com.ssafy.model.with.BoardMemberDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ListMemberAlarmDTO
import com.ssafy.model.with.ListMemberDTO
import com.ssafy.model.with.ReplyDTO
import com.ssafy.model.with.ReplyWithMemberDTO
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.with.WorkspaceMemberDTO
import com.ssafy.model.workspace.WorkSpaceDTO

// Member
fun MemberEntity.toDTO(): User {
    return User(
        nickname = this.nickname,
        email = this.email,
        profileImage = this.profileImageUrl
    )
}

fun MemberBackgroundEntity.toDTO(): BackgroundDto {
    return BackgroundDto(
        id = this.id,
        imgPath = this.url,
        color = 0,
        isStatus = this.isStatus
    )
}

// Workspace

fun WorkspaceEntity.toDTO(): WorkSpaceDTO {
    return WorkSpaceDTO(
        workSpaceId = this.id,
        authority = this.authority,
        name = this.name,
        isStatus = this.isStatus
    )
}

fun WorkspaceInBoard.toDTO(): WorkspaceInBoardDTO {
    return WorkspaceInBoardDTO(
        id = this.workspace.id,
        name = this.workspace.name,
        authority = this.workspace.authority,
        isStatus = this.workspace.isStatus,
        boards = this.board.map { it.toDTO() }
    )
}

fun BoardInList.toDTO(): BoardInListDTO {
    return BoardInListDTO(
        id = this.board.id,
        workspaceId = this.board.workspaceId,
        name = this.board.name,
        backgroundType = this.board.backgroundType,
        backgroundValue = this.board.backgroundValue,
        visibility = this.board.visibility,
        isClosed = this.board.isClosed,
        isStatus = this.board.isStatus,
        columnUpdate = this.board.columnUpdate,
        lists = this.lists.map { it.toDTO() },
        labels = this.labels.map { it.toDTO() },
        boardMembers = this.boardMembers.map { it.toDTO() },
        isBoardMyWatch = this.boardMemberAlarm?.isAlert ?: false
    )
}

fun ListInCards.toDTO(): ListInCardsDTO {
    return ListInCardsDTO(
        id = this.list.id,
        boardId = this.list.boardId,
        name = this.list.name,
        myOrder = this.list.myOrder,
        isArchived = this.list.isArchived,
        isStatus = this.list.isStatus,
        columnUpdate = this.list.columnUpdate,
        cards = this.cards.map { it.toDTO() },
        listMembers = this.listMembers.map { it.toDTO() },
        listMemberAlarm = this.listMemberAlarm?.isAlert ?: false
    )
}

fun CardAllInfo.toDTO(): CardAllInfoDTO {
    return CardAllInfoDTO(
        id = this.card.id,
        listId = this.card.listId,
        name = this.card.name,
        description = this.card.description,
        startAt = this.card.startAt,
        endAt = this.card.endAt,
        coverType = this.card.coverType,
        coverValue = this.card.coverValue,
        myOrder = this.card.myOrder,
        isArchived = this.card.isArchived,
        isStatus = this.card.isStatus,
        columnUpdate = this.card.columnUpdate,
        cardLabels = this.cardLabels.map { it.toDTO() },
        cardMembers = this.cardMembers.map { it.toDTO() },
        cardMemberAlarm = this.cardMemberAlarm?.isAlert ?: false,
        cardAttachment = this.cardAttachment.map { it.toDTO() },
        cardReplies = this.cardReplies.map { it.toDTO() }
    )
}

fun LabelEntity.toDTO(): LabelDTO {
    return LabelDTO(
        id = this.id,
        boardId = this.boardId,
        name = this.name,
        color = this.color,
        isStatus = this.isStatus
    )
}

fun BoardMemberEntity.toDTO(): BoardMemberDTO {
    return BoardMemberDTO(
        id = this.id,
        boardId = this.boardId,
        memberId = this.memberId,
        authority = this.authority,
        isStatus = this.isStatus
    )
}

fun ListMemberEntity.toDTO(): ListMemberDTO {
    return ListMemberDTO(
        id = this.id,
        memberId = this.memberId,
        listId = this.listId,
        isStatus = this.isStatus
    )
}

fun WorkspaceMemberEntity.toDTO(): WorkspaceMemberDTO {
    return WorkspaceMemberDTO(
        id = this.id,
        memberId = this.memberId,
        workspaceId = this.workspaceId,
        authority = this.authority,
        isStatus = this.isStatus
    )
}

fun ListMemberAlarmEntity.toDTO(): ListMemberAlarmDTO {
    return ListMemberAlarmDTO(
        isAlert = this.isAlert,
        listId = this.listId,
        isStatus = this.isStatus
    )
}

fun BoardMemberAlarmEntity.toDTO(): BoardMemberAlarmDTO {
    return BoardMemberAlarmDTO(
        isAlert = this.isAlert,
        boardId = this.boardId,
        isStatus = this.isStatus
    )
}

fun CardLabelEntity.toDTO(): CardLabelDTO {
    return CardLabelDTO(
        id = this.id,
        labelId = this.labelId,
        cardId = this.cardId,
        isActivated = this.isActivated,
        isStatus = this.isStatus
    )
}

fun CardMemberEntity.toDTO(): CardMemberDTO {
    return CardMemberDTO(
        id = this.id,
        memberId = this.memberId,
        cardId = this.cardId,
        isRepresentative = this.isRepresentative,
        isStatus = this.isStatus
    )
}

fun AttachmentEntity.toDTO(): AttachmentDTO {
    return AttachmentDTO(
        id = this.id,
        cardId = this.cardId,
        url = this.url,
        type = this.type,
        isCover = this.isCover,
        isStatus = this.isStatus
    )
}

fun ReplyEntity.toDTO(): ReplyDTO {
    return ReplyDTO(
        id = this.id,
        cardId = this.cardId,
        memberId = this.memberId,
        content = this.content,
        createAt = this.createAt,
        updateAt = this.updateAt,
        isStatus = this.isStatus
    )
}

fun WorkspaceMemberWithMemberInfo.toDTO(): MemberResponseDTO {
    return MemberResponseDTO(
        memberId = this.member.id,
        authority = this.workspaceMember.authority,
        memberEmail = this.member.email,
        memberNickname = this.member.nickname,
        memberProfileImgUrl = this.member.profileImageUrl,
        isStatus = this.workspaceMember.isStatus,
        isRepresentative = false,
        componentId = this.workspaceMember.workspaceId
    )
}

fun ListMemberWithMemberInfo.toDTO(): MemberResponseDTO {
    return MemberResponseDTO(
        memberId = this.member.id,
        memberEmail = this.member.email,
        memberNickname = this.member.nickname,
        memberProfileImgUrl = this.member.profileImageUrl,
        isStatus = this.listMember.isStatus,
        authority = "",
        isRepresentative = false,
        componentId = this.listMember.listId
    )
}

fun BoardMemberWithMemberInfo.toDTO(): MemberResponseDTO {
    return MemberResponseDTO(
        memberId = this.member.id,
        memberEmail = this.member.email,
        memberNickname = this.member.nickname,
        memberProfileImgUrl = this.member.profileImageUrl,
        componentId = this.boardMember.boardId,
        authority = this.boardMember.authority,
        isRepresentative = false,
        isStatus = this.boardMember.isStatus
    )
}

// Board
fun BoardEntity.toDto(): BoardDTO {
    return BoardDTO(
        id = this.id,
        workspaceId = this.workspaceId,
        name = this.name,
        background = Background(
            type = BackgroundType.valueOf(backgroundType ?: "COLOR"),
            value = backgroundValue ?: "0xff000000"
        ),
        isClosed = this.isClosed,
        visibility = Visibility.valueOf(visibility)
    )
}

// List
fun ListEntity.toDto(): ListResponseDto {
    return ListResponseDto(
        boardId = this.boardId,
        isStatus = this.isStatus,
        name = this.name,
        isArchived = this.isArchived,
        listId = this.id,
        myOrder = this.myOrder
    )
}

fun ListResponseDto.toEntity(): ListEntity {
    return ListEntity(
        boardId = this.boardId,
        isStatus = this.isStatus,
        name = this.name,
        isArchived = this.isArchived,
        myOrder = this.myOrder,
        id = this.listId
    )
}

// Reply
fun ReplyWithMemberDTO.toEntity(): ReplyWithMemberInfo {
    val replyEntity = ReplyEntity(
        id = this.id,
        cardId = this.cardId,
        memberId = this.memberId,
        content = this.content,
        createAt = this.createAt,
        updateAt = this.updateAt,
        isStatus = this.isStatus
    )

    val memberEntity = MemberEntity(
        id = this.memberId,
        email = this.memberEmail,
        nickname = this.memberNickname,
        profileImageUrl = this.memberProfileImgUrl
    )

    return ReplyWithMemberInfo(
        reply = replyEntity,
        member = memberEntity
    )
}