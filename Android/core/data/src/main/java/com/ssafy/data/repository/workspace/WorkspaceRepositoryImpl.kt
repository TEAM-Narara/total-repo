package com.ssafy.data.repository.workspace

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.network.source.workspace.WorkspaceDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val workspaceDataSource: WorkspaceDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WorkspaceRepository {

    override suspend fun getWorkspaceList(isConnected: Boolean): Flow<WorkSpaceListResponseDTO> =
        withContext(ioDispatcher) {
            if (isConnected) {
                workspaceDataSource.getWorkspaceList().toFlow()
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 가져오는 로직을 추가해주세요.")
            }
        }

    override suspend fun createWorkspace(
        workspaceRequestDTO: WorkspaceRequestDTO,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            workspaceDataSource.createWorkspace(workspaceRequestDTO).toFlow()
        } else {
            TODO("Room DB 연동이 되면 로컬 데이터를 생성하는 로직을 추가해주세요.")
        }
    }

    override suspend fun deleteWorkspace(workspaceId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                workspaceDataSource.deleteWorkspace(workspaceId).toFlow()

            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 삭제하는 로직을 추가해주세요.")
            }
        }

    override suspend fun updateWorkspace(
        workspaceId: Long,
        workspaceRequestDTO: WorkspaceRequestDTO,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            workspaceDataSource.updateWorkspace(workspaceId, workspaceRequestDTO).toFlow()
        } else {
            TODO("Room DB 연동이 되면 로컬 데이터를 수정하는 로직을 추가해주세요.")
        }
    }

    override suspend fun getWorkspaceMembers(workspaceId: Long): Flow<MemberListResponseDTO> =
        withContext(ioDispatcher) {
            TODO("Room DB 연동이 되면 로컬 데이터를 가져오는 로직을 추가해주세요.")
        }

    override suspend fun getWorkspacesByMember(memberId: Long): Flow<WorkSpaceListResponseDTO> =
        withContext(ioDispatcher) {
            TODO("Room DB 연동이 되면 로컬 데이터를 가져오는 로직을 추가해주세요.")
        }

}
