package com.ssafy.data.socket.board.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.image.ImageStorage
import com.ssafy.data.socket.board.model.board.AddBoardLabelRequestDto
import com.ssafy.data.socket.board.model.board.AddBoardMemberRequestDto
import com.ssafy.data.socket.board.model.board.DeleteBoardLabelRequestDto
import com.ssafy.data.socket.board.model.board.DeleteBoardMemberRequestDto
import com.ssafy.data.socket.board.model.board.EditBoardLabelRequestDto
import com.ssafy.data.socket.board.model.board.EditBoardMemberRequestDto
import com.ssafy.data.socket.board.model.board.EditBoardWatchRequestDto
import com.ssafy.database.dao.BoardMemberDao
import com.ssafy.database.dao.LabelDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.LabelEntity
import com.ssafy.database.dto.MemberEntity
import com.ssafy.model.member.Authority
import com.ssafy.model.with.DataStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardService @Inject constructor(
    private val memberDao: MemberDao,
    private val boardMemberDao: BoardMemberDao,
    private val labelDao: LabelDao,
    private val imageStorage: ImageStorage,
    private val gson: Gson
) {
    suspend fun addBoardMember(data: JsonObject) {
        val dto = gson.fromJson(data, AddBoardMemberRequestDto::class.java)

        memberDao.getMember(dto.memberId)?.let {
            imageStorage.saveAll(key = dto.profileImgUrl) { path ->
                memberDao.insertMember(
                    MemberEntity(
                        id = dto.memberId,
                        nickname = dto.memberName,
                        email = dto.memberEmail,
                        profileImageUrl = path,
                    )
                )
            }
        }

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
        boardMemberDao.deleteBoardMemberById(dto.boardMemberId)
    }

    suspend fun editBoardWatch(data: JsonObject) {
        val dto = gson.fromJson(data, EditBoardWatchRequestDto::class.java)
        boardMemberDao.updateBoardMemberAlarm(
            BoardMemberAlarmEntity(
                boardId = dto.boardId,
                isAlert = dto.isAlert,
                isStatus = DataStatus.STAY,
            )
        )
    }

    suspend fun addBoardLabel(data: JsonObject) {
        val dto = gson.fromJson(data, AddBoardLabelRequestDto::class.java)
        labelDao.insertLabel(
            LabelEntity(
                id = dto.labelId,
                boardId = dto.boardId,
                name = dto.name,
                color = dto.color
            )
        )
    }

    suspend fun editBoardLabel(data: JsonObject) {
        val dto = gson.fromJson(data, EditBoardLabelRequestDto::class.java)
        val before = labelDao.getLabel(dto.labelId) ?: throw Exception("존재하지 않는 라벨입니다.")
        labelDao.updateLabel(
            before.copy(
                name = dto.name,
                color = dto.color,
                isStatus = DataStatus.STAY,
                columnUpdate = 0,
            )
        )
    }

    suspend fun deleteBoardLabel(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteBoardLabelRequestDto::class.java)
        labelDao.deleteLabelByLabelId(dto.labelId)
    }
}