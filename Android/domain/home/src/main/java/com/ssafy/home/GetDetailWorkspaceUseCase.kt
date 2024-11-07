package com.ssafy.home

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.home.data.SettingData
import com.ssafy.home.data.toMemberData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDetailWorkspaceUseCase @Inject constructor(private val workspaceRepository: WorkspaceRepository) {

    suspend operator fun invoke(workspaceId: Long): Flow<SettingData?> {
        return combine(
            workspaceRepository.getWorkspace(workspaceId),
            workspaceRepository.getWorkspaceMembers(workspaceId)
        ) { workspace, members ->
            workspace?.let {
                SettingData(
                    workspaceId = workspaceId,
                    workspaceName = workspace.name,
                    members = members.map { it.toMemberData() }
                )
            }
        }
    }

}
