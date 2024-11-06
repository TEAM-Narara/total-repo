package com.ssafy.data.repository.list

import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.model.with.ListInCardsDTO
import kotlinx.coroutines.flow.Flow

interface ListRepository {

    suspend fun createList(
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun updateList(
        updateListRequestDto: UpdateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun setListArchive(listId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>>

    suspend fun getListDetails(boardId: Long): Flow<List<ListInCardsDTO>>
}
