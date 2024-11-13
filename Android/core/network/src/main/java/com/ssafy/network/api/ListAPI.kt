package com.ssafy.network.api

import com.ssafy.model.card.CardMoveUpdateRequestDTO
import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListMoveUpdateRequestDTO
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ListAPI {

    @POST("api/v1/list")
    suspend fun createList(@Body createListRequestDto: CreateListRequestDto): Response<ApiResponse<Unit>>

    @PATCH("api/v1/list/{listId}")
    suspend fun updateList(
        @Path("listId") listId: Long,
        @Body updateListRequestDto: UpdateListRequestDto
    ): Response<ApiResponse<Unit>>

    @PATCH("api/v1/list/move")
    suspend fun moveList(@Body listMoveUpdateRequestDTO: List<ListMoveUpdateRequestDTO>
    ): Response<ApiResponse<Unit>>

    @PATCH("api/v1/list/{listId}/archive")
    suspend fun setListArchive(@Path("listId") listId: Long): Response<ApiResponse<Unit>>

    @GET("api/v1/list/{boardId}/archived")
    suspend fun getArchivedLists(@Path("boardId") boardId: Long): Response<ApiResponse<List<ListResponseDto>>>

}
