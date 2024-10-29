package com.ssafy.model.card

import com.ssafy.model.search.Label

// TODO 변경될 수 있음!
data class CardDTO(
    val cardId: Long = 0L,
    val isWatching: Boolean = false,
    val content: String =
        "이 편지는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고 지금은 당신에게로 옮겨진 이 편지는 4일 안에 당신 곁을 떠나야 합니다. 이 편지를 포함해서 7통을 행운이 필요한 사람에게 보내 주셔야 합니다. 복사를 해도 좋습니다. 혹 미신이라 하실지 모르지만 사실입니다.",
    val title: String = "나는야 카드 제목",
    val boardTitle: String = "나는야 보드 제목",
    val listTitle: String = "나는야 리스트 제목",
    val startDate: Long = 1730041200000, // 2024-01-01 00:00:00
    val endDate: Long = 1730127600000, // 2024-01-02 00:00:00
    val labels: List<Label> = listOf(Label("trello", 0xFF0000FF), Label("ssafy", 0xFF00FF00)),
    // 멤버와 첨부파일의 이미지의 파일 경로가 담겨야 합니다.
    val members: List<String> = List(3) {
        "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z"
    },
    val attachments: List<String> = List(3) {
        "https://image.tving.com/ntgs/contents/CTC/caip/CAIP1500/ko/20231117/0450/P001749388.png/dims/resize/1280"
    },
    val comments: List<CommentDTO> = List(8) {
        CommentDTO(
            commentId = it.toLong(),
            userId = it.toLong(),
            nickname = "손오공",
            profileImageUrl = "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z",
            content = "# ㅋㅋㅋ 손오공 바보",
            date = 300,
        )
    }
//    val members : List<String> = emptyList(),
//    val attachments : List<String> = emptyList(),
//    val comments : List<CommentDTO> = emptyList(),
) {
    var editableContent: String = content
}
