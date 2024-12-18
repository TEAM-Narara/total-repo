package com.ssafy.home

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.home.data.DetailWorkspaceData
import com.ssafy.home.data.toMemberData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDetailWorkspaceUseCase @Inject constructor(private val workspaceRepository: WorkspaceRepository) {

    suspend operator fun invoke(workspaceId: Long): Flow<DetailWorkspaceData?> {
        return combine(
            workspaceRepository.getWorkspace(workspaceId),
            workspaceRepository.getWorkspaceMembers(workspaceId)
        ) { workspace, members ->
            workspace?.let {
                DetailWorkspaceData(
                    workspaceId = workspace.workspaceId,
                    workspaceName = workspace.name,
                    members = members.map { it.toMemberData() }
                )
            }
        }
    }

}
