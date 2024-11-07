package com.ssafy.data.repository.workspace

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toEntity
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.network.source.workspace.WorkspaceDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val workspaceDataSource: WorkspaceDataSource,
    private val workspaceDao: WorkspaceDao,
    private val workspaceMemberDao: WorkspaceMemberDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WorkspaceRepository {

    override suspend fun getWorkspaceList(isConnected: Boolean): Flow<List<WorkSpaceDTO>> =
        withContext(ioDispatcher) {
            if (isConnected) {
                workspaceDataSource.getWorkspaceList().map { dtoList ->
                    workspaceDao.insertWorkspaces(dtoList.map { it.toEntity() })
                }
            }

            getLocalScreenWorkspaceList()
        }

    override suspend fun getWorkspace(workspaceId: Long): Flow<WorkSpaceDTO?> =
        withContext(ioDispatcher) {
            workspaceDao.getWorkspaceForDrawable(workspaceId).map { it?.toDTO() }
        }

    override suspend fun getLocalScreenWorkspaceList(): Flow<List<WorkSpaceDTO>> =
        withContext(ioDispatcher) {
            workspaceDao.getAllWorkspaces()
                .map { entities -> entities.map { it.toDTO() } }
        }

    override suspend fun getLocalCreateWorkspaceList(): List<WorkspaceInBoardDTO> =
        withContext(ioDispatcher) {
            workspaceDao.getAllLocalCreateWorkspaces()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationWorkspaceList(): List<WorkSpaceDTO> =
        withContext(ioDispatcher) {
            workspaceDao.getAllLocalOperationWorkspaces()
                .map { it.toDTO() }
        }

    override suspend fun createWorkspace(
        name: String,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            workspaceDataSource.createWorkspace(name)
        } else {
            flow {
                workspaceDao.insertWorkspace(
                    WorkspaceEntity(
                        name = name,
                        authority = "ADMIN",
                        isStatus = "CREATE"
                    )
                )
            }
        }
    }

    override suspend fun deleteWorkspace(workspaceId: Long, isConnected: Boolean): Flow<Unit> {
        return withContext(ioDispatcher) {
            if (isConnected) {
                workspaceDataSource.deleteWorkspace(workspaceId)
            } else {
                val workspace = workspaceDao.getWorkspace(workspaceId)
                flowOf(workspaceDao.deleteWorkspace(workspace))
            }
        }
    }

    override suspend fun updateWorkspace(
        workspaceId: Long,
        name: String,
        isConnected: Boolean
    ): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            if (isConnected) {
                workspaceDataSource.updateWorkspace(workspaceId, name)
            } else {
                workspaceDao.updateWorkspace(workspaceId, name)
            }
        }
    }

    override suspend fun getWorkspaceMembers(workspaceId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            workspaceMemberDao.getWorkspaceMembers(workspaceId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun getWorkspacesByMember(memberId: Long): Flow<List<WorkSpaceDTO>> =
        withContext(ioDispatcher) {
            workspaceMemberDao.getWorkspacesByMember(memberId)
                .map { list -> list.map { it.toDTO() } }
        }
}
