package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.member.DetailMemberDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.network.api.WorkspaceAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkspaceDataSourceImpl @Inject constructor(
    private val workspaceAPI: WorkspaceAPI
) : WorkspaceDataSource {

    override suspend fun getWorkspaceList(): Flow<List<WorkSpaceDTO>> =
        safeApiCall { workspaceAPI.getWorkspaces() }.toFlow()

    override suspend fun createWorkspace(name: String): Flow<Long> =
        safeApiCall { workspaceAPI.createWorkspace(name) }.toFlow().map { it.workSpaceId }

    override suspend fun deleteWorkspace(workspaceId: Long): Flow<Unit> =
        safeApiCall { workspaceAPI.deleteWorkspace(workspaceId) }.toFlow()

    override suspend fun updateWorkspace(
        workspaceId: Long,
        name: String
    ): Flow<Unit> =
        safeApiCall { workspaceAPI.updateWorkspace(workspaceId, name) }.toFlow()

    override suspend fun getWorkspaceMembers(workspaceId: Long): Flow<List<MemberResponseDTO>> =
        safeApiCall { workspaceAPI.getWorkspaceMembers(workspaceId) }.toFlow()

    override suspend fun getWorkspacesByMember(memberId: Long): Flow<List<WorkSpaceDTO>> =
        safeApiCall { workspaceAPI.getWorkspacesByMember(memberId) }.toFlow()

    override suspend fun addWorkspaceMember(
        workspaceId: Long,
        simpleMemberDto: SimpleMemberDto
    ): Flow<DetailMemberDto> =
        safeApiCall { workspaceAPI.addWorkspaceMember(workspaceId, simpleMemberDto) }.toFlow()

    override suspend fun deleteWorkspaceMember(
        workspaceId: Long,
        memberId: Long
    ): Flow<DetailMemberDto> =
        safeApiCall { workspaceAPI.deleteWorkspaceMember(workspaceId, memberId) }.toFlow()

    override suspend fun updateWorkspaceMember(
        workspaceId: Long,
        detailMemberDto: SimpleMemberDto
    ): Flow<DetailMemberDto> =
        safeApiCall { workspaceAPI.updateWorkspaceMember(workspaceId, detailMemberDto) }.toFlow()

}
