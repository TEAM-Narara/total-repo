package com.ssafy.data.repository.workspace

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.with.BoardMemberDTO
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.with.WorkspaceMemberDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {

    suspend fun getWorkspaceList(isConnected: Boolean): Flow<List<WorkSpaceDTO>>

    suspend fun getWorkspace(workspaceId: Long): Flow<WorkSpaceDTO?>

    suspend fun createWorkspace(name: String, isConnected: Boolean): Flow<Long>

    suspend fun getLocalScreenWorkspaceList(): Flow<List<WorkSpaceDTO>>

    suspend fun getLocalCreateWorkspaceList(): List<WorkspaceInBoardDTO>

    suspend fun getLocalOperationWorkspaceList(): List<WorkSpaceDTO>

    suspend fun deleteWorkspace(workspaceId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateWorkspace(
        workspaceId: Long,
        name: String,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun getWorkspaceMemberMyInfo(workspaceId: Long, memberId: Long): Flow<WorkspaceMemberDTO?>

    suspend fun getWorkspaceMembers(workspaceId: Long): Flow<List<MemberResponseDTO>>

    suspend fun getWorkspacesByMember(memberId: Long): Flow<List<WorkSpaceDTO>>

    suspend fun addWorkspaceMember(
        workspaceId: Long,
        simpleMemberDto: SimpleMemberDto
    ): Flow<Unit>

    suspend fun deleteWorkspaceMember(
        workspaceId: Long,
        memberId: Long,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun updateWorkspaceMember(
        workspaceId: Long,
        simpleMemberDto: SimpleMemberDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun getLocalOperationWorkspaceMember(): List<WorkspaceMemberDTO>
}
