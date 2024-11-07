package com.ssafy.data.repository.list

import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.model.with.ListInCard
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ListMemberAlarmDTO
import com.ssafy.model.with.ListMemberDTO
import kotlinx.coroutines.flow.Flow

interface ListRepository {

    suspend fun createList(
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Long>

    suspend fun updateList(
        updateListRequestDto: UpdateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun deleteList(listId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun setListArchive(listId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getLists(boardId: Long): Flow<List<ListResponseDto>>

    suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>>

    suspend fun getLocalCreateList(): List<ListInCardsDTO>

    suspend fun getLocalOperationList(): List<ListResponseDto>

    suspend fun getListMembers(listId: Long): Flow<List<MemberResponseDTO>>

    suspend fun deleteListMember(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getListWatchStatus(id: Long): Flow<Boolean>

    suspend fun toggleListWatch(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getLocalOperationListMember(): List<ListMemberDTO>

    suspend fun getLocalOperationListMemberAlarm(): List<ListMemberAlarmDTO>

    suspend fun getLocalScreenListsInCards(boardId: Long): Flow<List<ListInCard>>
}
