package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.datastore.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteWorkspaceMemberUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(
        workspaceId: Long,
        memberId: Long,
        isConnected: Boolean
    ): Flow<Unit> = flow {
        val myMemberId = dataStoreRepository.getUser().memberId
        if (myMemberId == memberId) throw IllegalArgumentException("자기 자신은 삭제할 수 없습니다.")

        workspaceRepository.deleteWorkspaceMember(workspaceId, memberId, isConnected)
            .collect { emit(it) }
    }

}
