package com.ssafy.network.source.list

import com.ssafy.model.list.ListRequestDto
import com.ssafy.model.list.ListResponseDto
import kotlinx.coroutines.flow.Flow

interface ListDataSource {

    suspend fun createList(listRequestDto: ListRequestDto): Flow<Unit>

    suspend fun updateList(
        listId: Long,
        listRequestDto: ListRequestDto
    ): Flow<Unit>

    suspend fun setListArchive(listId: Long): Flow<Unit>

    suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>>

}
