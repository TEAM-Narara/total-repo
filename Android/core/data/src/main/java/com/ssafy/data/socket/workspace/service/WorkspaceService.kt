package com.ssafy.data.socket.workspace.service

import com.google.gson.Gson
import com.ssafy.data.socket.workspace.model.AddWorkspaceBoardRequestDto
import com.ssafy.data.socket.workspace.model.AddWorkspaceMemberDto
import com.ssafy.data.socket.workspace.model.DeleteWorkSpaceRequestDto
import com.ssafy.data.socket.workspace.model.DeleteWorkspaceBoardRequestDto
import com.ssafy.data.socket.workspace.model.DeleteWorkspaceMemberRequestDto
import com.ssafy.data.socket.workspace.model.EditWorkSpaceRequestDto
import com.ssafy.data.socket.workspace.model.EditWorkspaceMemberRequestDto
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.WorkspaceMemberEntity
import com.ssafy.model.with.DataStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceService @Inject constructor(
    private val workspaceDao: WorkspaceDao,
    private val workspaceMemberDao: WorkspaceMemberDao,
    private val boardDao: BoardDao,
    private val gson: Gson
) {
    suspend fun deleteWorkSpace(data: String) {
        val dto = gson.fromJson(data, DeleteWorkSpaceRequestDto::class.java)
        workspaceDao.deleteByWorkspaceId(dto.workspaceId)
    }

    suspend fun editWorkSpace(data: String) {
        val dto = gson.fromJson(data, EditWorkSpaceRequestDto::class.java)
        val before = workspaceDao.getWorkspace(dto.workspaceId) ?: throw Exception("존재하지 않는 workspace 입니다.")
        workspaceDao.updateWorkspace(before.copy(name = dto.workspaceName))
    }

    suspend fun addMember(data: String) {
        val dto = gson.fromJson(data, AddWorkspaceMemberDto::class.java)
        workspaceMemberDao.insertWorkspaceMembers(
            listOf(
                WorkspaceMemberEntity(
                    id = dto.memberId,
                    workspaceId = dto.workspaceId,
                    authority = dto.authority,
                    isStatus = DataStatus.STAY
                )
            )
        )
    }

    suspend fun deleteMember(data: String) {
        val dto = gson.fromJson(data, DeleteWorkspaceMemberRequestDto::class.java)
        workspaceMemberDao.deleteByWorkspaceId(dto.workspaceId, dto.memberId)
    }

    suspend fun editMember(data: String) {
        val dto = gson.fromJson(data, EditWorkspaceMemberRequestDto::class.java)
        val before = workspaceMemberDao.getWorkspaceMemberByWorkspaceIdAndMemberId(
            dto.workspaceId,
            dto.memberId
        )
        workspaceMemberDao.updateWorkspaceMember(
            before?.copy(
                authority = dto.authority,
                isStatus = DataStatus.STAY
            ) ?: throw Exception("존재하지 않는 workspace 입니다.")
        )
    }

    suspend fun addBoard(data: String) {
        val dto = gson.fromJson(data, AddWorkspaceBoardRequestDto::class.java)
        boardDao.insertBoard(
            BoardEntity(
                id = dto.boardId,
                workspaceId = dto.workspaceId,
                name = dto.boardName,
                coverType = dto.backgroundType,
                coverValue = dto.backgroundValue,
                visibility = dto.visibility,
                isClosed = dto.isClosed,
            )
        )
    }

    suspend fun deleteBoard(data: String) {
        val dto = gson.fromJson(data, DeleteWorkspaceBoardRequestDto::class.java)
        boardDao.deleteBoardByBoardId(dto.boardId)
    }
}