package com.ssafy.model.activity.boardmember

data class DeleteBoardMemberInfo(
    val memberId: Long,
    val memberNickname : String,
    val boardId : Long,
    val boardName : String,
){
    override fun toString(): String {
        return memberNickname
    }
}
