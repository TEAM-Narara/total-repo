package com.ssafy.data.repository.workspace

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toEntity
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.with.WorkspaceMemberDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.network.source.workspace.WorkspaceDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
                val dtoList = workspaceDataSource.getWorkspaceList().firstOrNull()
                dtoList?.let {
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
    ): Flow<Long> =
        withContext(ioDispatcher) {
            if (isConnected) {
                workspaceDataSource.createWorkspace(name).map { 5 }
            } else {
                flowOf(workspaceDao.insertWorkspace(
                    WorkspaceEntity(
                        name = name,
                        authority = "ADMIN",
                        isStatus = DataStatus.CREATE
                    )
                ))
            }
        }

    override suspend fun deleteWorkspace(workspaceId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val workspace = workspaceDao.getWorkspace(workspaceId)

            if (workspace != null) {
                if (isConnected) {
                    workspaceDataSource.deleteWorkspace(workspaceId)
                } else {
                    when (workspace.isStatus) {
                        DataStatus.CREATE ->
                            flowOf(workspaceDao.deleteLocalWorkspace(workspace))

                        else ->
                            flowOf(workspaceDao.updateWorkspace(workspace.copy(isStatus = DataStatus.DELETE)))
                    }
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun updateWorkspace(
        workspaceId: Long,
        name: String,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val workspace = workspaceDao.getWorkspace(workspaceId)

            if (workspace != null) {
                if (isConnected) {
                    workspaceDataSource.updateWorkspace(workspaceId, name)
                } else {
                    val result = when (workspace.isStatus) {
                        DataStatus.STAY ->
                            workspaceDao.updateWorkspace(
                                workspace.copy(
                                    name = name,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE, DataStatus.UPDATE ->
                            workspaceDao.updateWorkspace(workspace.copy(name = name))

                        DataStatus.DELETE -> {}
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
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

    override suspend fun addWorkspaceMember(
        workspaceId: Long,
        simpleMemberDto: SimpleMemberDto
    ): Flow<Unit> = withContext(ioDispatcher) {
        workspaceDataSource.addWorkspaceMember(workspaceId, simpleMemberDto)
            // TODO SOCKET 원래는 소켓오면 저장해야하는데 지금은 그냥 저장 (테스트용)
//            .map { member: DetailMemberDto ->
//                val workspaceEntity = member.toWorkspaceMemberEntity(workspaceId)
//                workspaceMemberDao.insertWorkspaceMember(workspaceEntity)
//                Unit
//            }
            .map { Unit }
    }

    override suspend fun deleteWorkspaceMember(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val workspaceMember = workspaceMemberDao.getWorkspaceMember(id)

            if (workspaceMember != null) {
                if (isConnected) {
                    workspaceDataSource.deleteWorkspaceMember(id).map { Unit }
                } else {
                    val result = when (workspaceMember.isStatus) {
                        DataStatus.CREATE -> workspaceMemberDao.deleteLocalWorkspaceMember(workspaceMember)
                        else -> workspaceMemberDao.updateWorkspaceMember(workspaceMember.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun updateWorkspaceMember(
        id: Long,
        simpleMemberDto: SimpleMemberDto,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val workspaceMember = workspaceMemberDao.getWorkspaceMember(id)

            if (workspaceMember != null) {
                if (isConnected) {
                    workspaceDataSource.updateWorkspaceMember(id, simpleMemberDto).map { Unit }
                } else {
                    val result = when (workspaceMember.isStatus) {
                        DataStatus.STAY ->
                            workspaceMemberDao.updateWorkspaceMember(
                                workspaceMember.copy(
                                    isStatus = DataStatus.UPDATE,
                                    authority = simpleMemberDto.authority
                                )
                            )

                        DataStatus.CREATE, DataStatus.UPDATE ->
                            workspaceMemberDao.updateWorkspaceMember(workspaceMember.copy(authority = simpleMemberDto.authority))

                        DataStatus.DELETE -> {}
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

//    override suspend fun deleteWorkspaceMember(id: Long, isConnected: Boolean): Flow<Unit> = flow {
//        withContext(ioDispatcher) {
//            val workspaceMember = workspaceMemberDao.getWorkspaceMember(id)
//
//            if (workspaceMember != null) {
//                if (isConnected) {
//                    workspaceDataSource.deleteWorkspaceMember(id)
//                } else {
//                    when (workspaceMember.isStatus) {
//                        DataStatus.CREATE ->
//                            workspaceMemberDao.deleteLocalWorkspaceMember(workspaceMember)
//
//                        else ->
//                            workspaceMemberDao.updateWorkspaceMember(workspaceMember.copy(isStatus = DataStatus.DELETE))
//                    }
//                }
//            }
//        }
//    }
//
//    override suspend fun updateWorkspaceMember(
//        id: Long,
//        authority: String,
//        isConnected: Boolean
//    ): Flow<Unit> = flow {
//        withContext(ioDispatcher) {
//            val workspaceMember = workspaceMemberDao.getWorkspaceMember(id)
//
//            if (workspaceMember != null) {
//                if (isConnected) {
//                    workspaceDataSource.updateWorkspaceMember(id, authority)
//                } else {
//                    when (workspaceMember.isStatus) {
//                        DataStatus.STAY ->
//                            workspaceMemberDao.updateWorkspaceMember(
//                                workspaceMember.copy(
//                                    isStatus = DataStatus.UPDATE,
//                                    authority = authority
//                                )
//                            )
//
//                        DataStatus.CREATE, DataStatus.UPDATE ->
//                            workspaceMemberDao.updateWorkspaceMember(workspaceMember.copy(authority = authority))
//
//                        DataStatus.DELETE -> {}
//                    }
//                }
//            }
//        }
//    }

    override suspend fun getLocalOperationWorkspaceMember(): List<WorkspaceMemberDTO> =
        withContext(ioDispatcher) {
            workspaceMemberDao.getLocalOperationWorkspaceMember()
                .map { it.toDTO() }
        }
}
