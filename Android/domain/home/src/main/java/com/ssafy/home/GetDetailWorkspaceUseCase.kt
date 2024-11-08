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
            workspaceRepository.getWorkspace(workspaceId) ?: throw Exception("존재하지 않는 workspace 입니다."),
            workspaceRepository.getWorkspaceMembers(workspaceId)
        ) { workspace, members ->
            workspace?.let {
                DetailWorkspaceData(
                    workspaceId = workspaceId,
                    workspaceName = workspace.name,
                    members = members.map { it.toMemberData() }
                )
            }
        }
    }

}
