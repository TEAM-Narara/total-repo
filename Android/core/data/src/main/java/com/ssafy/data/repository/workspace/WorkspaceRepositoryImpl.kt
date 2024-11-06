package com.ssafy.data.repository.workspace

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toEntity
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.network.source.workspace.WorkspaceDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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

    override suspend fun getLocalCreateWorkspaceList(): Flow<List<WorkspaceInBoardDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocalOperationWorkspaceList(): Flow<List<WorkSpaceDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun createWorkspace(
        name: String,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            workspaceDataSource.createWorkspace(name)
        } else {
            TODO("Room DB 연동이 되면 로컬 데이터를 생성하는 로직을 추가해주세요.")
        }
    }

    override suspend fun deleteWorkspace(workspaceId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                workspaceDataSource.deleteWorkspace(workspaceId)

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
            workspaceDataSource.updateWorkspace(workspaceId, workspaceRequestDTO)
        } else {
            TODO("Room DB 연동이 되면 로컬 데이터를 수정하는 로직을 추가해주세요.")
        }
    }

    override suspend fun getWorkspaceMembers(workspaceId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            TODO("Room DB 연동이 되면 로컬 데이터를 가져오는 로직을 추가해주세요.")
        }

    override suspend fun getWorkspacesByMember(memberId: Long): Flow<List<WorkSpaceDTO>> =
        withContext(ioDispatcher) {
            TODO("Room DB 연동이 되면 로컬 데이터를 가져오는 로직을 추가해주세요.")
        }

}
