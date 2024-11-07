package com.ssafy.data.repository.workspace

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toEntity
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.with.WorkspaceMemberDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.network.source.workspace.WorkspaceDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override suspend fun getLocalScreenWorkspaceList(): Flow<List<WorkSpaceDTO>> =
        withContext(ioDispatcher) {
            workspaceDao.getAllWorkspaces()
                .map { entities -> entities.map { it.toDTO() } }
        }

    override suspend fun getLocalCreateWorkspaceList(): List<WorkspaceInBoardDTO> =
        withContext(ioDispatcher) {
            workspaceDao.getLocalCreateWorkspaces()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationWorkspaceList(): List<WorkSpaceDTO> =
        withContext(ioDispatcher) {
            workspaceDao.getLocalOperationWorkspaces()
                .map { it.toDTO() }
        }

    override suspend fun createWorkspace(
        name: String,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            workspaceDataSource.createWorkspace(name)
        } else {
           flow { workspaceDao.insertWorkspace(WorkspaceEntity(name = name, authority = "ADMIN", isStatus = DataStatus.CREATE)) }
        }
    }

    override suspend fun deleteWorkspace(workspaceId: Long, isConnected: Boolean): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val workspace = workspaceDao.getWorkspace(workspaceId)

            if(workspace != null) {
                if (isConnected) {
                    workspaceDataSource.deleteWorkspace(workspaceId)
                } else {
                    when(workspace.isStatus) {
                        DataStatus.CREATE ->
                            workspaceDao.deleteLocalWorkspace(workspace)
                        else ->
                            workspaceDao.updateWorkspace(workspace.copy(isStatus = DataStatus.DELETE))
                    }
                }
            }
        }
    }

    override suspend fun updateWorkspace(
        workspaceId: Long,
        name: String,
        isConnected: Boolean
    ): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val workspace = workspaceDao.getWorkspace(workspaceId)

            if(workspace != null) {
                if (isConnected) {
                    workspaceDataSource.updateWorkspace(workspaceId, name)
                } else {
                    when(workspace.isStatus) {
                        DataStatus.STAY ->
                            workspaceDao.updateWorkspace(workspace.copy(name = name, isStatus = DataStatus.UPDATE))
                        DataStatus.CREATE, DataStatus.UPDATE  ->
                            workspaceDao.updateWorkspace(workspace.copy(name = name))
                        DataStatus.DELETE -> { }
                    }
                }
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

    override suspend fun deleteWorkspaceMember(id: Long, isConnected: Boolean): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val workspaceMember = workspaceMemberDao.getWorkspaceMember(id)

            if(workspaceMember != null) {
                if (isConnected) {
                    workspaceDataSource.deleteWorkspaceMember(id)
                } else {
                    when(workspaceMember.isStatus) {
                        DataStatus.CREATE ->
                            workspaceMemberDao.deleteLocalWorkspaceMember(workspaceMember)
                        else ->
                            workspaceMemberDao.updateWorkspaceMember(workspaceMember.copy(isStatus = DataStatus.DELETE))
                    }
                }
            }
        }
    }

    override suspend fun updateWorkspaceMember(
        id: Long,
        authority: String,
        isConnected: Boolean
    ): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val workspaceMember = workspaceMemberDao.getWorkspaceMember(id)

            if(workspaceMember != null) {
                if (isConnected) {
                    workspaceDataSource.updateWorkspaceMember(id, authority)
                } else {
                    when(workspaceMember.isStatus) {
                        DataStatus.STAY ->
                            workspaceMemberDao.updateWorkspaceMember(workspaceMember.copy(isStatus = DataStatus.UPDATE, authority = authority))
                        DataStatus.CREATE, DataStatus.UPDATE  ->
                            workspaceMemberDao.updateWorkspaceMember(workspaceMember.copy(authority = authority))
                        DataStatus.DELETE -> { }
                    }
                }
            }
        }
    }

    override suspend fun getLocalOperationWorkspaceMember(): List<WorkspaceMemberDTO> =
        withContext(ioDispatcher) {
            workspaceMemberDao.getLocalOperationWorkspaceMember()
                .map { it.toDTO() }
        }
}
