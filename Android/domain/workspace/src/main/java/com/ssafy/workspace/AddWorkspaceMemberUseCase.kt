package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.member.Authority
import com.ssafy.model.member.SimpleMemberDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddWorkspaceMemberUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(workspaceId: Long, memberId: Long): Flow<Unit> {
        val authority = Authority.MEMBER
        val simpleMemberDto = SimpleMemberDto(memberId, authority)
        return workspaceRepository.addWorkspaceMember(workspaceId, simpleMemberDto)
    }

}
