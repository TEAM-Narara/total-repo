package com.ssafy.board

import androidx.paging.PagingData
import androidx.paging.map
import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.model.activity.BoardActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBoardActivityUseCase @Inject constructor(
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long): Flow<PagingData<BoardActivity>> =
        boardRepository.getBoardActivity(boardId).map { value: PagingData<BoardActivity> ->
            value.map { boardActivity: BoardActivity ->
                // TODO BoardActivity 메시지 변환


                val member = boardActivity.activity.who
                val profile =
                    memberRepository.getMember(member.memberId).firstOrNull()?.profileImgUrl

                val newBoardActivity = boardActivity.copy(
                    activity = boardActivity.activity.copy(
                        who = member.copy(memberProfileImageUrl = profile)
                    )
                )


                newBoardActivity
            }
        }

}
