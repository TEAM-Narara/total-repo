package com.ssafy.data.socket.workspace.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.socket.workspace.model.AddWorkspaceBoardRequestDto
import com.ssafy.data.socket.workspace.model.AddWorkspaceMemberDto
import com.ssafy.data.socket.workspace.model.DeleteWorkSpaceRequestDto
import com.ssafy.data.socket.workspace.model.DeleteWorkspaceBoardRequestDto
import com.ssafy.data.socket.workspace.model.DeleteWorkspaceMemberRequestDto
import com.ssafy.data.socket.workspace.model.EditArchiveBoardRequestDto
import com.ssafy.data.socket.workspace.model.EditWorkSpaceRequestDto
import com.ssafy.data.socket.workspace.model.EditWorkspaceBoardRequestDto
import com.ssafy.data.socket.workspace.model.EditWorkspaceMemberRequestDto
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.WorkspaceMemberEntity
import com.ssafy.model.member.Authority
import com.ssafy.model.with.DataStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceService @Inject constructor(
    private val workspaceDao: WorkspaceDao,
    private val workspaceMemberDao: WorkspaceMemberDao,
    private val memberDao: MemberDao,
    private val boardDao: BoardDao,
    private val gson: Gson
) {
    suspend fun deleteWorkSpace(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteWorkSpaceRequestDto::class.java)
        workspaceDao.deleteByWorkspaceId(dto.workspaceId)
    }

    suspend fun editWorkSpace(data: JsonObject) {
        val dto = gson.fromJson(data, EditWorkSpaceRequestDto::class.java)
        val before = workspaceDao.getWorkspace(dto.workspaceId)
            ?: throw Exception("존재하지 않는 workspace 입니다.")
        workspaceDao.updateWorkspace(
            before.copy(
                name = dto.workspaceName,
                isStatus = DataStatus.STAY
            )
        )
    }

    suspend fun addMember(data: JsonObject) {
        val dto = gson.fromJson(data, AddWorkspaceMemberDto::class.java)

        memberDao.insertMember(
            MemberEntity(
                id = dto.memberId,
                nickname = dto.memberName,
                email = dto.memberEmail,
                profileImageUrl = dto.profileImgUrl,
            )
        )

        workspaceMemberDao.insertWorkspaceMember(
            WorkspaceMemberEntity(
                id = dto.workspaceMemberId,
                memberId = dto.memberId,
                workspaceId = dto.workspaceId,
                authority = Authority.valueOf(dto.authority),
                isStatus = DataStatus.STAY
            )
        )
    }

    suspend fun deleteMember(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteWorkspaceMemberRequestDto::class.java)
        workspaceMemberDao.deleteLocalWorkspaceMember(
            workspaceId = dto.workspaceId, memberId = dto.memberId)
    }

    suspend fun editMember(data: JsonObject) {
        val dto = gson.fromJson(data, EditWorkspaceMemberRequestDto::class.java)
        val before = workspaceMemberDao.getWorkspaceMember(dto.workspaceMemberId)
            ?: throw Exception("워크스페이스에 존재하지 않는 맴버 입니다.")
        workspaceMemberDao.updateWorkspaceMember(
            before.copy(
                authority = dto.authority,
                isStatus = DataStatus.STAY
            )
        )
    }

    suspend fun addBoard(data: JsonObject) {
        val dto = gson.fromJson(data, AddWorkspaceBoardRequestDto::class.java)
        // TODO : 이미지 저장 로직 구현
        boardDao.insertBoard(
            BoardEntity(
                id = dto.boardId,
                workspaceId = dto.workspaceId,
                name = dto.boardName,
                coverType = dto.coverType,
                coverValue = dto.coverValue,
                visibility = dto.visibility,
                isClosed = dto.isClosed,
            )
        )
    }

    suspend fun editBoard(data: JsonObject) {
        val dto = gson.fromJson(data, EditWorkspaceBoardRequestDto::class.java)
        val before = boardDao.getBoard(dto.boardId) ?: throw Exception("존재하지 않는 보드입니다.")
        // TODO : 이미지 저장 로직 구현
        boardDao.updateBoard(
            before.copy(
                name = dto.boardName,
                coverType = dto.coverType,
                coverValue = dto.coverValue,
                visibility = dto.visibility,
                isClosed = dto.isClosed,
                isStatus = DataStatus.STAY,
            )
        )
    }

    suspend fun deleteBoard(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteWorkspaceBoardRequestDto::class.java)
        boardDao.deleteBoardByBoardId(dto.boardId)
    }

    suspend fun editArchivedBoard(data: JsonObject) {
        val dto = gson.fromJson(data, EditArchiveBoardRequestDto::class.java)
        val before = boardDao.getBoard(dto.boardId) ?: throw Exception("존재하지 않는 보드입니다.")
        boardDao.updateBoard(
            before.copy(
                isClosed = dto.isArchive
            )
        )
    }
}