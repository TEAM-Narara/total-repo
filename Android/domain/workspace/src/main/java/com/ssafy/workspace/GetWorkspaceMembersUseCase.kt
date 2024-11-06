package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.board.MemberResponseDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkspaceMembersUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {
    
    suspend operator fun invoke(workspaceId: Long): Flow<List<MemberResponseDTO>> {
        return workspaceRepository.getWorkspaceMembers(workspaceId)
    }

}
