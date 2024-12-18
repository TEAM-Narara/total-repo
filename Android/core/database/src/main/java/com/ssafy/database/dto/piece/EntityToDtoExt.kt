package com.ssafy.database.dto.piece

import com.ssafy.database.dto.AttachmentEntity
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.CardMemberAlarmEntity
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
import com.ssafy.database.dto.bitmask.CardCoverBitmask
import com.ssafy.database.dto.bitmask.CoverBitmask
import com.ssafy.database.dto.bitmask.UpdateBoardArchiveBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateBoardBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateCardArchiveBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateCardBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateCardListIdBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateCardOrderBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateLabelBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateListArchiveBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateListBitmaskDTO
import com.ssafy.database.dto.bitmask.UpdateListOrderBitmaskDTO
import com.ssafy.database.dto.with.BoardInList
import com.ssafy.database.dto.with.BoardMemberWithMemberInfo
import com.ssafy.database.dto.with.CardAllInfo
import com.ssafy.database.dto.with.CardLabelWithLabelInfo
import com.ssafy.database.dto.with.CardMemberWithMemberInfo
import com.ssafy.database.dto.with.CardWithListAndBoardName
import com.ssafy.database.dto.with.ListInCards
import com.ssafy.database.dto.with.ListMemberWithMemberInfo
import com.ssafy.database.dto.with.MemberWithRepresentative
import com.ssafy.database.dto.with.ReplyWithMemberInfo
import com.ssafy.database.dto.with.WorkspaceInBoard
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo
import com.ssafy.model.background.Cover
import com.ssafy.model.background.CoverDto
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.Visibility
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.member.Authority
import com.ssafy.model.user.User
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.BoardInListDTO
import com.ssafy.model.with.BoardMemberAlarmDTO
import com.ssafy.model.with.BoardMemberDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardLabelWithLabelDTO
import com.ssafy.model.with.CardMemberAlarmDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.CardThumbnail
import com.ssafy.model.with.CardWithListAndBoardNameDTO
import com.ssafy.model.with.CoverType
import com.ssafy.model.with.ListInCard
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ListMemberAlarmDTO
import com.ssafy.model.with.ListMemberDTO
import com.ssafy.model.with.MemberWithRepresentativeDTO
import com.ssafy.model.with.ReplyDTO
import com.ssafy.model.with.ReplyWithMemberDTO
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.with.WorkspaceMemberDTO
import com.ssafy.model.workspace.WorkSpaceDTO

// Member
fun MemberEntity.toDTO(): User {
    return User(
        memberId = this.id,
        nickname = this.nickname,
        email = this.email,
        profileImgUrl = this.profileImageUrl
    )
}

fun MemberBackgroundEntity.toDTO(): CoverDto {
    return CoverDto(
        id = this.id,
        imgPath = this.url,
        color = 0,
        isStatus = this.isStatus
    )
}

// Workspace
fun WorkspaceEntity.toDTO(): WorkSpaceDTO {
    return WorkSpaceDTO(
        workspaceId = this.id,
        authority = Authority.valueOf(this.authority.name),
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

// WORKSPACE MEMBER
fun WorkspaceMemberEntity.toDTO(): WorkspaceMemberDTO {
    return WorkspaceMemberDTO(
        id = this.id,
        memberId = this.memberId,
        workspaceId = this.workspaceId,
        authority = this.authority,
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

// BOARD
fun BoardEntity.toDto(): BoardDTO {
    return BoardDTO(
        id = this.id,
        workspaceId = this.workspaceId,
        name = this.name,
        cover = Cover(
            type = CoverType.valueOf(coverType ?: "COLOR"),
            value = coverValue ?: "0xff000000"
        ),
        isClosed = this.isClosed,
        visibility = Visibility.valueOf(visibility)
    )
}

// BOARD
fun BoardEntity.toBitDto(): UpdateBoardBitmaskDTO {
    return UpdateBoardBitmaskDTO(
        name = this.name,
        cover = CoverBitmask(
            type = coverType?.let { CoverType.valueOf(it) },
            value = coverValue
        ),
        visibility = Visibility.valueOf(visibility)
    )
}

fun BoardEntity.toBitArchiveDto(): UpdateBoardArchiveBitmaskDTO {
    return UpdateBoardArchiveBitmaskDTO(
        isClosed = this.isClosed
    )
}

fun BoardInList.toDTO(): BoardInListDTO {
    return BoardInListDTO(
        id = this.board.id,
        workspaceId = this.board.workspaceId,
        name = this.board.name,
        coverType = this.board.coverType,
        coverValue = this.board.coverValue,
        visibility = this.board.visibility,
        isClosed = this.board.isClosed,
        isStatus = this.board.isStatus,
        lists = this.lists.map { it.toDTO() },
        labels = this.labels.map { it.toDTO() },
        boardMembers = this.boardMembers.map { it.toDTO() },
        isBoardMyWatch = this.boardMemberAlarm?.isAlert ?: false
    )
}

// BOARD MEMBER
fun BoardMemberEntity.toDTO(): BoardMemberDTO {
    return BoardMemberDTO(
        id = this.id,
        boardId = this.boardId,
        memberId = this.memberId,
        authority = this.authority,
        isStatus = this.isStatus
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

fun BoardMemberAlarmEntity.toDTO(): BoardMemberAlarmDTO {
    return BoardMemberAlarmDTO(
        isAlert = this.isAlert,
        boardId = this.boardId,
        isStatus = this.isStatus
    )
}

// LABEL
fun LabelEntity.toDTO(): LabelDTO {
    return LabelDTO(
        labelId = this.id,
        boardId = this.boardId,
        labelName = this.name,
        labelColor = this.color,
        isStatus = this.isStatus
    )
}

fun LabelEntity.toBitDto(): UpdateLabelBitmaskDTO {
    return UpdateLabelBitmaskDTO(
        name = this.name,
        color = this.color
    )
}

// LIST

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

fun ListEntity.toBitDto(): UpdateListBitmaskDTO {
    return UpdateListBitmaskDTO(
        name = this.name,
    )
}

fun ListEntity.toBitOrderDto(): UpdateListOrderBitmaskDTO {
    return UpdateListOrderBitmaskDTO(
        myOrder = this.myOrder,
    )
}

fun ListEntity.toBitArchiveDto(): UpdateListArchiveBitmaskDTO {
    return UpdateListArchiveBitmaskDTO(
        isArchived = this.isArchived,
    )
}

fun ListEntity.toDto(cards: List<CardThumbnail> = emptyList(), isWatch: Boolean): ListInCard {
    return ListInCard(
        id = this.id,
        name = this.name,
        myOrder = this.myOrder,
        isArchived = this.isArchived,
        cards = cards,
        isStatus = this.isStatus,
        isWatch = isWatch
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
        cards = this.cards.map { it.toDTO() },
        listMembers = this.listMembers.map { it.toDTO() },
        listMemberAlarm = this.listMemberAlarm?.isAlert ?: false
    )
}

// LIST MEMBER
fun ListMemberEntity.toDTO(): ListMemberDTO {
    return ListMemberDTO(
        id = this.id,
        memberId = this.memberId,
        listId = this.listId,
        isStatus = this.isStatus
    )
}

fun ListMemberWithMemberInfo.toDTO(): MemberResponseDTO {
    return MemberResponseDTO(
        memberId = this.member.id,
        memberEmail = this.member.email,
        memberNickname = this.member.nickname,
        memberProfileImgUrl = this.member.profileImageUrl,
        isStatus = this.listMember.isStatus,
        authority = Authority.MEMBER,
        isRepresentative = false,
        componentId = this.listMember.listId
    )
}

fun ListMemberAlarmEntity.toDTO(): ListMemberAlarmDTO {
    return ListMemberAlarmDTO(
        isAlert = this.isAlert,
        listId = this.listId,
        isStatus = this.isStatus
    )
}

// CARD
fun CardEntity.toDto(): CardResponseDto {
    return CardResponseDto(
        id = this.id,
        listId = this.listId,
        name = this.name,
        description = this.description,
        startAt = this.startAt,
        endAt = this.endAt,
        cover = Cover(
            type = CoverType.valueOf(coverType ?: "COLOR"),
            value = coverValue ?: "0xff000000"
        ),
        myOrder = this.myOrder,
        isArchived = this.isArchived,
        isStatus = this.isStatus,
        columnUpdate = this.columnUpdate
    )
}

fun CardEntity.toBitDto(): UpdateCardBitmaskDTO {
    return UpdateCardBitmaskDTO(
        name = this.name,
        description = this.description,
        startAt = this.startAt,
        endAt = this.endAt,
        cover = CardCoverBitmask(
            type = coverType?.let { CoverType.valueOf(it) },
            value = coverValue
        )
    )
}

fun CardEntity.toListIdBitDto(): UpdateCardListIdBitmaskDTO {
    return UpdateCardListIdBitmaskDTO(
        listId = this.listId
    )
}

fun CardEntity.toOrderBitDto(): UpdateCardOrderBitmaskDTO {
    return UpdateCardOrderBitmaskDTO(
        myOrder = this.myOrder
    )
}

fun CardEntity.toArchiveBitDto(): UpdateCardArchiveBitmaskDTO {
    return UpdateCardArchiveBitmaskDTO(
        isArchived = this.isArchived
    )
}

fun CardEntity.toDTO(
    replyCount: Int = 0,
    isWatch: Boolean = false,
    isAttachment: Boolean = false,
    cardMembers: List<MemberResponseDTO> = emptyList(),
    cardLabels: List<CardLabelWithLabelDTO> = emptyList()
): CardThumbnail {
    return CardThumbnail(
        id = this.id,
        listId = this.listId,
        name = this.name,
        description = this.description,
        startAt = this.startAt,
        endAt = this.endAt,
        coverType = this.coverType,
        coverValue = this.coverValue,
        myOrder = this.myOrder,
        isArchived = this.isArchived,
        replyCount = replyCount,
        cardMembers = cardMembers,
        cardLabels = cardLabels,
        isStatus = this.isStatus,
        isAttachment = isAttachment,
        isWatch = isWatch
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
        cover = Cover(
            type = CoverType.valueOf(card.coverType ?: "NONE"),
            value = card.coverValue ?: "NONE"
        ),
        myOrder = this.card.myOrder,
        isArchived = this.card.isArchived,
        isStatus = this.card.isStatus,
        cardLabels = this.cardLabels.map { it.toDTO() },
        cardMembers = this.cardMembers.map { it.toDTO() },
        cardMemberAlarm = this.cardMemberAlarm?.isAlert ?: false,
        cardAttachment = this.cardAttachment.map { it.toDTO() },
        cardReplies = this.cardReplies.map { it.toDTO() }
    )
}

// CARD MEMBER
fun CardMemberEntity.toDTO(): CardMemberDTO {
    return CardMemberDTO(
        id = this.id,
        memberId = this.memberId,
        cardId = this.cardId,
        isRepresentative = this.isRepresentative,
        isStatus = this.isStatus
    )
}

fun CardMemberWithMemberInfo.toDTO(): MemberResponseDTO {
    return MemberResponseDTO(
        memberId = this.member.id,
        memberEmail = this.member.email,
        memberNickname = this.member.nickname,
        memberProfileImgUrl = this.member.profileImageUrl,
        componentId = this.cardMember.cardId,
        authority = Authority.MEMBER,
        isRepresentative = this.cardMember.isRepresentative,
        isStatus = this.cardMember.isStatus
    )
}

fun CardMemberAlarmEntity.toDTO(): CardMemberAlarmDTO {
    return CardMemberAlarmDTO(
        isAlert = this.isAlert,
        cardId = this.cardId,
        isStatus = this.isStatus
    )
}

// CARD LABEL

fun CardLabelEntity.toDTO(): CardLabelDTO {
    return CardLabelDTO(
        cardLabelId = this.id,
        labelId = this.labelId,
        cardId = this.cardId,
        isActivated = this.isActivated,
        isStatus = this.isStatus
    )
}

fun CardLabelWithLabelInfo.toDto(): CardLabelWithLabelDTO {
    return CardLabelWithLabelDTO(
        cardLabelId = this.cardLabel.id,
        labelId = this.cardLabel.labelId,
        cardId = this.cardLabel.cardId,
        isActivated = this.cardLabel.isActivated,
        cardLabelStatus = this.cardLabel.isStatus,

        labelBoardId = this.label.boardId,
        labelName = this.label.name,
        labelColor = this.label.color,
        labelStatus = this.label.isStatus
    )
}

// Attachment
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

// Reply
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

fun CardWithListAndBoardName.toDTO() : CardWithListAndBoardNameDTO = with(this) {
    CardWithListAndBoardNameDTO(
        cardId = cardId,
        cardName = cardName,
        listName = listName,
        boardName = boardName,
    )
}

fun MemberWithRepresentative.toDTO(): MemberWithRepresentativeDTO = with(this) {
    MemberWithRepresentativeDTO(
        memberId = memberId,
        email = email,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        isRepresentative = isRepresentative
    )
}