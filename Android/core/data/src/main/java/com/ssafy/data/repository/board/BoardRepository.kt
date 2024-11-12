package com.ssafy.data.repository.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.with.BoardInListDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.label.UpdateLabelRequestDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.with.BoardMemberAlarmDTO
import com.ssafy.model.with.BoardMemberDTO
import com.ssafy.model.with.DataStatus
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    suspend fun createBoard(myMemberId: Long, boardDTO: BoardDTO, isConnected: Boolean): Flow<Long>

    suspend fun getBoard(boardId: Long): Flow<BoardDTO?>

    suspend fun deleteBoard(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateBoard(id: Long, updateBoardRequestDto: UpdateBoardRequestDto, isConnected: Boolean): Flow<Unit>

    suspend fun setBoardArchive(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getBoardsByWorkspace(id: Long): Flow<List<BoardDTO>>

    suspend fun getLocalCreateBoardList(): List<BoardInListDTO>

    suspend fun getLocalOperationBoardList(): List<BoardDTO>

    suspend fun getArchivedBoardsByWorkspace(id: Long): Flow<List<BoardDTO>>

    suspend fun createBoardWatch(boardId: Long, isStatus: DataStatus): Flow<Long>

    suspend fun getWatchStatus(id: Long): Flow<Boolean?>

    suspend fun toggleBoardWatch(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getBoardMemberMyInfo(boardId: Long, memberId: Long): Flow<BoardMemberDTO?>

    suspend fun getBoardMembers(boardId: Long): Flow<List<MemberResponseDTO>>

    suspend fun createBoardMember(boardId: Long, memberId: Long, isConnected: Boolean): Flow<Long>

    suspend fun deleteBoardMember(boardId: Long, memberId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateBoardMember(boardId: Long, simpleMemberDto: SimpleMemberDto, isConnected: Boolean): Flow<Unit>

    suspend fun getLocalOperationBoardMember(): List<BoardMemberDTO>

    suspend fun getLocalOperationBoardMemberAlarm(): List<BoardMemberAlarmDTO>

    suspend fun createLabel(labelDTO: LabelDTO, isConnected: Boolean): Flow<Long>

    suspend fun getLabel(id: Long): Flow<LabelDTO?>

    suspend fun getLabels(boardId: Long): Flow<List<LabelDTO>>

    suspend fun deleteLabel(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateLabel(id: Long, updateLabelRequestDto: UpdateLabelRequestDto, isConnected: Boolean): Flow<Unit>

    suspend fun getLocalCreateLabels(): List<LabelDTO>

    suspend fun getLocalOperationLabels(): List<LabelDTO>
}
