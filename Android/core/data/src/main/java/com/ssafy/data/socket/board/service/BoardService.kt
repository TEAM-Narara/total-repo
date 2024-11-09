package com.ssafy.data.socket.board.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.socket.board.model.AddBoardMemberRequestDto
import com.ssafy.data.socket.board.model.DeleteBoardMemberRequestDto
import com.ssafy.data.socket.board.model.EditBoardMemberRequestDto
import com.ssafy.data.socket.board.model.EditBoardWatchRequestDto
import com.ssafy.database.dao.BoardMemberDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.MemberEntity
import com.ssafy.model.member.Authority
import com.ssafy.model.with.DataStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardService @Inject constructor(
    private val memberDao: MemberDao,
    private val boardMemberDao: BoardMemberDao,
    private val gson: Gson
) {
    suspend fun addBoardMember(data: JsonObject) {
        val dto = gson.fromJson(data, AddBoardMemberRequestDto::class.java)
        memberDao.insertMember(
            MemberEntity(
                id = dto.memberId,
                email = dto.memberEmail,
                nickname = dto.memberName,
                profileImageUrl = dto.profileImgUrl,
            )
        )

        boardMemberDao.insertBoardMember(
            BoardMemberEntity(
                id = dto.boardMemberId,
                memberId = dto.memberId,
                boardId = dto.boardId,
                authority = Authority.valueOf(dto.authority),
            )
        )
    }

    suspend fun editBoardMember(data: JsonObject) {
        val dto = gson.fromJson(data, EditBoardMemberRequestDto::class.java)
        val before = boardMemberDao.getBoardMember(dto.boardMemberId)
            ?: throw Exception("보드에 존재하지 않는 멤버입니다.")
        boardMemberDao.updateBoardMember(
            before.copy(
                authority = Authority.valueOf(dto.authority),
                isStatus = DataStatus.STAY
            )
        )
    }

    suspend fun deleteBoardMember(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteBoardMemberRequestDto::class.java)
        boardMemberDao.deleteBoardMemberByBoardIdAndMemberId(dto.boardId, dto.memberId)
    }

    suspend fun editBoardWatch(data: JsonObject) {
        val dto = gson.fromJson(data, EditBoardWatchRequestDto::class.java)
        boardMemberDao.updateBoardMemberAlarm(
            BoardMemberAlarmEntity(
                boardId = dto.boardId,
                isAlert = dto.isAlert,
            )
        )
    }
}