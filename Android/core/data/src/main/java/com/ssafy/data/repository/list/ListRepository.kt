package com.ssafy.data.repository.list

import com.ssafy.model.list.ListRequestDto
import com.ssafy.model.list.ListResponseDto
import kotlinx.coroutines.flow.Flow

interface ListRepository {

    suspend fun createList(
        listRequestDto: ListRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun updateList(
        listId: Long,
        listRequestDto: ListRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun setListArchive(listId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>>

}
