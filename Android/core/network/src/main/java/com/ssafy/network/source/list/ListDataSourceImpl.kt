package com.ssafy.network.source.list

import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.network.api.ListAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListDataSourceImpl @Inject constructor(private val listAPI: ListAPI) : ListDataSource {

    override suspend fun createList(createListRequestDto: CreateListRequestDto): Flow<Unit> =
        safeApiCall { listAPI.createList(createListRequestDto) }.toFlow()

    override suspend fun updateList(updateListRequestDto: UpdateListRequestDto): Flow<Unit> =
        safeApiCall { listAPI.updateList(updateListRequestDto.listId, updateListRequestDto) }.toFlow()

    override suspend fun deleteList(listId: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun setListArchive(listId: Long): Flow<Unit> =
        safeApiCall { listAPI.setListArchive(listId) }.toFlow()

    override suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>> =
        safeApiCall { listAPI.getArchivedLists(boardId) }.toFlow()

    override suspend fun deleteListMember(id: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }
}
