package com.ssafy.model.activity


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
    val target: Any,
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

enum class EventType {
    CREATE, UPDATE, DELETE
}

enum class EventData {
    BOARD, CARD, LIST
}

// TODO Target 경우의 수