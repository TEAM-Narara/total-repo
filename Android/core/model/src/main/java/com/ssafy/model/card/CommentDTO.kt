package com.ssafy.model.card


data class CommentDTO(
    val commentId: Long = 1L,
    val userId: Long = 1L,
    val email: String = "superboard@example.com",
    val nickname: String = "손오공",
    val profileImageUrl: String? = "https://an2-img.amz.wtchn.net/image/v2/h6S3XfqeRo7KBUmE9ArtBA.jpg?jwt=ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKdmNIUnpJanBiSW1SZk1USTRNSGczTWpCeE9EQWlYU3dpY0NJNklpOTJNaTl6ZEc5eVpTOXBiV0ZuWlM4eE5qRTFPRGN5T0RNd05UazJOVFF4TWpRNUluMC5OOTZYYXplajFPaXdHaWFmLWlmTjZDU1AzczFRXzRQcW4zM0diQmR4bC1z",
    val content: String = "",
    val createDate: Long = 0L,
    val updateDate: Long = 0L,

    @Transient
    val isFocus: Boolean = false
) {
    var editableContent: String = content
}