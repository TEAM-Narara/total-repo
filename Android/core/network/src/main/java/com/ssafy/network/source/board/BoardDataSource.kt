package com.ssafy.network.source.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.label.CreateLabelRequestDto
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.label.UpdateLabelRequestDto
import com.ssafy.model.member.SimpleMemberDto
import kotlinx.coroutines.flow.Flow

interface BoardDataSource {

    suspend fun createBoard(boardDTO: BoardDTO): Flow<BoardDTO>

    suspend fun getBoard(id: Long): Flow<BoardDTO>

    suspend fun deleteBoard(id: Long): Flow<Unit>

    suspend fun updateBoard(id: Long, updateBoardRequestDto: UpdateBoardRequestDto): Flow<Unit>

    suspend fun setBoardArchive(boardId: Long): Flow<Unit>

    suspend fun getBoardsByWorkspace(workspaceId: Long): Flow<List<BoardDTO>>

    suspend fun getArchivedBoardsByWorkspace(workspaceId: Long): Flow<List<BoardDTO>>

    suspend fun getWatchStatus(boardId: Long): Flow<Boolean>

    suspend fun toggleWatchBoard(boardId: Long): Flow<Unit>

    suspend fun getBoardMembers(boardId: Long): Flow<List<MemberResponseDTO>>

    suspend fun createLabel(boardId: Long, createLabelRequestDto: CreateLabelRequestDto): Flow<LabelDTO>

    suspend fun deleteLabel(id: Long): Flow<Unit>

    suspend fun updateLabel(id: Long, updateLabelRequestDto: UpdateLabelRequestDto): Flow<LabelDTO>

    suspend fun createBoardMember(boardId: Long, memberId: Long): Flow<MemberResponseDTO>

    suspend fun deleteBoardMember(boardId: Long, memberId: Long): Flow<MemberResponseDTO>

    suspend fun updateBoardMember(
        boardId: Long,
        simpleMemberDto: SimpleMemberDto
    ): Flow<MemberResponseDTO>
}
