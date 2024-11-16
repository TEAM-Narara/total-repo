package com.ssafy.network.source.list

import com.ssafy.model.alert.AlertResponse
import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListMoveUpdateListRequestDTO
import com.ssafy.model.list.ListMoveUpdateRequestDTO
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.nullable.UpdateListWithNull
import kotlinx.coroutines.flow.Flow

interface ListDataSource {

    suspend fun createList(createListRequestDto: CreateListRequestDto): Flow<ListResponseDto>

    suspend fun updateList(listId: Long, updateListRequestDto: UpdateListRequestDto): Flow<Unit>
    suspend fun updateList(listId: Long, updateListWithNull: UpdateListWithNull): Flow<Unit>

    suspend fun moveList(listMoveUpdateRequestDTO: ListMoveUpdateListRequestDTO): Flow<Unit>

    suspend fun deleteList(listId: Long): Flow<Unit>

    suspend fun setListArchive(listId: Long): Flow<Unit>

    suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>>

    suspend fun deleteListMember(memberId: Long, listId: Long): Flow<Unit>

    suspend fun toggleListWatchBoard(memberId: Long, listId: Long): Flow<AlertResponse>
}