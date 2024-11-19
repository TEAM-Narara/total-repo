package com.ssafy.model.activity.cardmember

data class RepresentativeStatusChangeInfo(
    val memberId: Long,
    val memberNickname: String,
    val cardId: Long,
    val cardName: String,
    val isRepresentative: Boolean,
) {
    override fun toString(): String {
        return memberNickname
    }
}
