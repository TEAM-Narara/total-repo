package com.ssafy.model.activity

import com.ssafy.model.activity.List.ArchiveListInfo
import com.ssafy.model.activity.List.CreateListInfo
import com.ssafy.model.activity.List.UpdateListInfo
import com.ssafy.model.activity.attachment.AddAttachmentInfo
import com.ssafy.model.activity.attachment.DeleteAttachmentInfo
import com.ssafy.model.activity.board.ArchiveStatusChangeInfo
import com.ssafy.model.activity.board.BoardWhereInfo
import com.ssafy.model.activity.board.CreateBoardInfo
import com.ssafy.model.activity.board.DeleteBoardInfo
import com.ssafy.model.activity.board.UpdateBoardInfo
import com.ssafy.model.activity.boardmember.AddBoardMemberInfo
import com.ssafy.model.activity.boardmember.DeleteBoardMemberInfo
import com.ssafy.model.activity.card.CardWhereInfo
import com.ssafy.model.activity.card.CreateCardInfo
import com.ssafy.model.activity.card.DeleteCardInfo
import com.ssafy.model.activity.card.UpdateCardInfo
import com.ssafy.model.activity.cardmember.RepresentativeStatusChangeInfo
import com.ssafy.model.activity.reply.ReplyInfo
import kotlin.reflect.KClass


data class BoardActivityDto(
    val boardActivityList: List<BoardActivity>,
    val totalElements: Long,
    val totalPages: Long,
)

data class BoardActivity(
    val activity: Activity,
    val `when`: Long,
    val message: String?
)

data class Activity(
    val eventData: EventData,
    val eventType: EventType,
    val target: Map<String, Any>,
    val `when`: Long,
    val `where`: Where,
    val who: Who
)

data class Who(
    val memberId: Long,
    val memberNickname: String,
    val memberProfileImageUrl: String?
)

data class Where(
    val boardId: Long,
    val boardName: String
)

// ADD = 라벨 할당, REMOVE = 라벨 해제
enum class EventType(val message: String) {
    CREATE("추가하였습니다."),
    UPDATE("수정하였습니다."),
    DELETE("삭제되었습니다."),
    MOVE("이동하였습니다."),
    ARCHIVE("아카이브로 이동하였습니다."),
    ADD("라벨이 할당되었습니다."),
    GRANT("권한을 허용하였습니다."),
    REVOKE("권한을 해제하였습니다.")
}

enum class EventData(val message: String) {
    BOARD("보드"),
    CARD("카드"),
    LIST("리스트"),
    COMMENT("댓글"),
    WORKSPACE("워크 스페이스"),
    LABEL("라벨"),
    ATTACHMENT("첨부 파일"),
    ASSIGNED("담당자"),
    BOARD_MEMBER("보드 회원"),
    LIST_MEMBER("리스트 회원"),
    LIST_MANAGER("리스트 관리자"),
    CARD_MEMBER("카드 회원"),
    CARD_MANAGER("카드 관리자"),
    CARD_COVER("카드 커버"),
    BOARD_COVER("보드 커버"),
    WORKSPACE_MEMBER("워크 스페이스 회원"),
}

// TODO Target 경우의 수
fun getClass(eventType: EventType, eventData: EventData): KClass<*> {
    if (eventData == EventData.ATTACHMENT) {
        if (eventType == EventType.CREATE) return AddAttachmentInfo::class
        else if (eventType == EventType.DELETE) return DeleteAttachmentInfo::class
    } else if (eventData == EventData.BOARD) {
        if (eventType == EventType.ARCHIVE) return ArchiveStatusChangeInfo::class
        else if (eventType == EventType.MOVE) return BoardWhereInfo::class
        else if (eventType == EventType.CREATE) return CreateBoardInfo::class
        else if (eventType == EventType.UPDATE) return UpdateBoardInfo::class
        else if (eventType == EventType.DELETE) return DeleteBoardInfo::class
    } else if (eventData == EventData.BOARD_MEMBER) {
        if (eventType == EventType.ADD) return AddBoardMemberInfo::class
        else if (eventType == EventType.DELETE) return DeleteBoardMemberInfo::class
    } else if (eventData == EventData.CARD) {
        if (eventType == EventType.ARCHIVE) return com.ssafy.model.activity.card.ArchiveStatusChangeInfo::class
        else if (eventType == EventType.MOVE) return CardWhereInfo::class
        else if (eventType == EventType.CREATE) return CreateCardInfo::class
        else if (eventType == EventType.UPDATE) return UpdateCardInfo::class
        else if (eventType == EventType.DELETE) return DeleteCardInfo::class
    } else if (eventData == EventData.CARD_MEMBER) {
        return RepresentativeStatusChangeInfo::class
    } else if (eventData == EventData.LIST) {
        if (eventType == EventType.ARCHIVE) return ArchiveListInfo::class
        else if (eventType == EventType.CREATE) return CreateListInfo::class
        else if (eventType == EventType.UPDATE) return UpdateListInfo::class
    } else if (eventData == EventData.COMMENT) {
        return ReplyInfo::class
    }

    return Nothing::class
}