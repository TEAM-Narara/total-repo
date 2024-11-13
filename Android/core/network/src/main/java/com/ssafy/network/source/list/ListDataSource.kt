package com.ssafy.network.source.list

import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListMoveUpdateRequestDTO
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import kotlinx.coroutines.flow.Flow

interface ListDataSource {

    suspend fun createList(createListRequestDto: CreateListRequestDto): Flow<Unit>

    suspend fun updateList(listId: Long, updateListRequestDto: UpdateListRequestDto): Flow<Unit>

    suspend fun moveList(listMoveUpdateRequestDTO: List<ListMoveUpdateRequestDTO>): Flow<Unit>

    suspend fun deleteList(listId: Long): Flow<Unit>

    suspend fun setListArchive(listId: Long): Flow<Unit>

    suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>>

    suspend fun deleteListMember(memberId: Long, listId: Long): Flow<Unit>

    suspend fun toggleListWatchBoard(listId: Long): Flow<Unit>
}