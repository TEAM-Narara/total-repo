package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkspacesByMemberUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(memberId: Long): Flow<List<WorkSpaceDTO>> {
        return workspaceRepository.getWorkspacesByMember(memberId)
    }

}