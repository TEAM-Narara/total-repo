package com.ssafy.data.repository.list

import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.bitmask.UpdateListBitmaskDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.list.ListMoveUpdateRequestDTO
import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ListInCard
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ListMemberAlarmDTO
import com.ssafy.model.with.ListMemberDTO
import kotlinx.coroutines.flow.Flow

interface ListRepository {

    suspend fun createList(
        myMemberId: Long,
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Long>

    suspend fun updateList(
        listId: Long,
        updateListRequestDto: UpdateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun moveList(boardId: Long,
                         listMoveUpdateRequestDTO: List<ListMoveUpdateRequestDTO>,
                         isConnected: Boolean
    ) : Flow<Unit>

    suspend fun updateList(listId: Long, dto: UpdateListBitmaskDTO)

    suspend fun deleteList(listId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun setListArchive(listId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getLists(boardId: Long): Flow<List<ListResponseDto>>

    suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>>

    suspend fun getLocalCreateList(): List<ListInCardsDTO>

    suspend fun getLocalOperationList(): List<ListEntity>

    suspend fun getListMembers(listId: Long): Flow<List<MemberResponseDTO>>

    suspend fun createListMember(listId: Long, memberId: Long, isStatus: DataStatus): Flow<Long>

    suspend fun deleteListMember(memberId: Long, listId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getListWatchStatus(id: Long): Flow<Boolean?>

    suspend fun createListWatch(listId: Long, isStatus: DataStatus): Flow<Long>

    suspend fun toggleListWatch(memberId: Long, listId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getLocalOperationListMember(): List<ListMemberDTO>

    suspend fun getLocalOperationListMemberAlarm(): List<ListMemberAlarmDTO>

    suspend fun getLocalScreenListsInCards(boardId: Long): Flow<List<ListInCard>>

    suspend fun getLocalScreenListsInCardsFilter(
        boardId: Long,
        includeNoRepresentative: Int,
        memberIdsEmpty: Int,
        memberIds: List<Long>,
        noLimitDate: Int,
        expireDate: Int,
        deadlineDateType: Int,
        includeNoLabel: Int,
        labelIdsEmpty: Int,
        cardLabelIds: List<Long>,
        keyword: String
    ): Flow<List<ListInCard>>
}
