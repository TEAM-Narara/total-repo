package com.ssafy.home

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.home.data.DetailWorkspaceData
import com.ssafy.home.data.toMemberData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetDetailWorkspaceUseCase @Inject constructor(private val workspaceRepository: WorkspaceRepository) {

    suspend operator fun invoke(workspaceId: Long): Flow<DetailWorkspaceData?> {

        val workspaceFlow = workspaceRepository.getWorkspace(workspaceId) ?: return flowOf(null)

        return combine(
            workspaceFlow,
            workspaceRepository.getWorkspaceMembers(workspaceId)
        ) { workspace, members ->
            DetailWorkspaceData(
                workspaceId = workspaceId,
                workspaceName = workspace.name,
                members = members.map { it.toMemberData() }
            )
        }
    }

}
