package com.ssafy.model.activity.boardmember

data class AddBoardMemberInfo(
    val memberId: Long,
    val memberNickname: String,
    val boardId: Long,
    val boardName: String,
){
    override fun toString(): String {
        return memberNickname
    }
}
