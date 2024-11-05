package com.ssafy.network.source.list

import com.ssafy.model.list.ListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.network.api.ListAPI
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListDataSourceImpl @Inject constructor(private val listAPI: ListAPI) : ListDataSource {

    override suspend fun createList(listRequestDto: ListRequestDto): Flow<Unit> =
        listAPI.createList(listRequestDto).toFlow()

    override suspend fun updateList(
        listId: Long,
        listRequestDto: ListRequestDto
    ): Flow<Unit> = listAPI.updateList(listId, listRequestDto).toFlow()

    override suspend fun setListArchive(listId: Long): Flow<Unit> =
        listAPI.setListArchive(listId).toFlow()

    override suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>> =
        listAPI.getArchivedLists(boardId).toFlow()

}
