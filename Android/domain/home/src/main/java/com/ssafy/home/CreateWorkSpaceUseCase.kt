package com.ssafy.home

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.datastore.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateWorkSpaceUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val workSpaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(isConnected: Boolean): Flow<Unit> {
        val userName = dataStoreRepository.getUser().nickname
        val workspaceName = "$userName'의 워크 스페이스"
        return workSpaceRepository.createWorkspace(workspaceName, isConnected)
    }

}
