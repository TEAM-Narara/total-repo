package com.ssafy.workspace

import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.member.Authority
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddWorkspaceMemberUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository,
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(workspaceId: Long, user: User): Flow<Unit> {
        val authority = Authority.MEMBER
        val simpleMemberDto = SimpleMemberDto(user.memberId, authority)
        return memberRepository.addMember(user).map {
            workspaceRepository.addWorkspaceMember(workspaceId, simpleMemberDto)
        }
    }

}
