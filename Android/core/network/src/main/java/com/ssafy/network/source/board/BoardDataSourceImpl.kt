package com.ssafy.network.source.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.label.UpdateLabelRequestDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.with.CoverType
import com.ssafy.network.api.BoardAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import com.ssafy.network.util.S3ImageUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BoardDataSourceImpl @Inject constructor(
    private val boardAPI: BoardAPI,
    private val s3ImageUtil: S3ImageUtil
) : BoardDataSource {

    override suspend fun createBoard(boardDTO: BoardDTO): Flow<BoardDTO> {
        val coverValue = if (boardDTO.cover.type == CoverType.IMAGE) {
            val key = "${boardDTO.id}/cover"
            s3ImageUtil.uploadS3Image(boardDTO.cover.value, key)
            key
        } else {
            boardDTO.cover.value
        }

        val newBoardDTO = boardDTO.copy(cover = boardDTO.cover.copy(value = coverValue))
        return safeApiCall { boardAPI.createBoard(newBoardDTO) }.toFlow()
    }

    override suspend fun getBoard(id: Long): Flow<BoardDTO> =
        safeApiCall { boardAPI.getBoard(id) }.toFlow() // TODO : Socket으로 바꾸기

    override suspend fun deleteBoard(id: Long): Flow<Unit> =
        safeApiCall { boardAPI.deleteBoard(id) }.toFlow()

    override suspend fun updateBoard(
        id: Long,
        updateBoardRequestDto: UpdateBoardRequestDto
    ): Flow<Unit> {
        val coverValue = if (updateBoardRequestDto.cover.type == CoverType.IMAGE) {
            val key = "${id}/cover"
            s3ImageUtil.uploadS3Image(updateBoardRequestDto.cover.value, key)
            key
        } else {
            updateBoardRequestDto.cover.value
        }

        val newUpdateBoardRequestDto =
            updateBoardRequestDto.copy(cover = updateBoardRequestDto.cover.copy(value = coverValue))
        return safeApiCall { boardAPI.updateBoard(id, newUpdateBoardRequestDto) }.toFlow()
    }

    override suspend fun setBoardArchive(boardId: Long): Flow<Unit> =
        safeApiCall { boardAPI.setBoardArchive(boardId) }.toFlow()

    override suspend fun getBoardsByWorkspace(workspaceId: Long): Flow<List<BoardDTO>> =
        safeApiCall { boardAPI.getBoardsByWorkspace(workspaceId) }.toFlow()

    override suspend fun getArchivedBoardsByWorkspace(workspaceId: Long): Flow<List<BoardDTO>> =
        safeApiCall { boardAPI.getArchivedBoardsByWorkspace(workspaceId) }.toFlow()

    override suspend fun getWatchStatus(boardId: Long): Flow<Boolean> =
        safeApiCall { boardAPI.getWatchStatus(boardId) }.toFlow()

    override suspend fun toggleWatchBoard(boardId: Long): Flow<Unit> =
        safeApiCall { boardAPI.toggleWatchBoard(boardId) }.toFlow()

    override suspend fun getBoardMembers(boardId: Long): Flow<List<MemberResponseDTO>> =
        safeApiCall { boardAPI.getBoardMembers(boardId) }.toFlow()

    override suspend fun createLabel(labelDTO: LabelDTO): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLabel(id: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLabel(
        id: Long,
        updateLabelRequestDto: UpdateLabelRequestDto
    ): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createBoardMember(boardId: Long, memberId: Long): Flow<MemberResponseDTO> =
        safeApiCall { boardAPI.createBoardMember(boardId, mapOf("memberId" to memberId)) }.toFlow()

    override suspend fun deleteBoardMember(boardId: Long, memberId: Long): Flow<MemberResponseDTO> =
        safeApiCall { boardAPI.deleteBoardMember(boardId, mapOf("memberId" to memberId)) }.toFlow()

    override suspend fun updateBoardMember(
        boardId: Long,
        simpleMemberDto: SimpleMemberDto
    ): Flow<MemberResponseDTO> =
        safeApiCall { boardAPI.updateBoardMember(boardId, simpleMemberDto) }.toFlow()

}
