package com.ssafy.network.source.list

import com.ssafy.model.list.ListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.network.api.ListAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class ListDataSourceImpl @Inject constructor(private val listAPI: ListAPI) : ListDataSource {

    override suspend fun createList(listRequestDto: ListRequestDto): Response<ApiResponse<Unit>> =
        listAPI.createList(listRequestDto)

    override suspend fun updateList(
        listId: Long,
        listRequestDto: ListRequestDto
    ): Response<ApiResponse<Unit>> = listAPI.updateList(listId, listRequestDto)

    override suspend fun setListArchive(listId: Long): Response<ApiResponse<Unit>> =
        listAPI.setListArchive(listId)

    override suspend fun getArchivedLists(boardId: Long): Response<ApiResponse<List<ListResponseDto>>> =
        listAPI.getArchivedLists(boardId)

}
