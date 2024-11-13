package com.ssafy.board

import androidx.paging.PagingData
import androidx.paging.map
import com.google.gson.Gson
import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.model.activity.BoardActivity
import com.ssafy.model.activity.getClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.reflect.KClass

class GetBoardActivityUseCase @Inject constructor(
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long): Flow<PagingData<BoardActivity>> =
        boardRepository.getBoardActivity(boardId).map { value: PagingData<BoardActivity> ->
            value.map { boardActivity: BoardActivity ->
                val member = boardActivity.activity.who
                val profile =
                    memberRepository.getMember(member.memberId).firstOrNull()?.profileImgUrl

                val newBoardActivity = boardActivity.copy(
                    activity = boardActivity.activity.copy(
                        who = member.copy(memberProfileImageUrl = profile)
                    ),
                    message = makeMessage(boardActivity)
                )

                newBoardActivity
            }
        }


    private fun makeMessage(boardActivity: BoardActivity): String = with(boardActivity.activity) {

        val classValue: KClass<*> = getClass(eventData = eventData, eventType = eventType)
        val eventInfo = try {
            val gson = Gson()
            val jsonString = gson.toJson(target)
            val event = gson.fromJson(jsonString, classValue.java).toString()
            "($event)"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        buildString {
            append(who.memberNickname)
            append("님이 ")
            append(" 보드")
            append(where.boardName)
            append("에 ")

            append(eventData.message)
            append(eventInfo)
            append("을(를) ")
            append(eventType.message)
        }
    }

}
