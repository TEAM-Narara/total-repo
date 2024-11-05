package com.ssafy.network.source.list

import com.ssafy.model.list.ListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response

interface ListDataSource {

    suspend fun createList(listRequestDto: ListRequestDto): Response<ApiResponse<Unit>>

    suspend fun updateList(
        listId: Long,
        listRequestDto: ListRequestDto
    ): Response<ApiResponse<Unit>>

    suspend fun setListArchive(listId: Long): Response<ApiResponse<Unit>>

    suspend fun getArchivedLists(boardId: Long): Response<ApiResponse<List<ListResponseDto>>>

}
