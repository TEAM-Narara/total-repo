package com.ssafy.network.source.list

import com.ssafy.model.alert.AlertResponse
import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.network.api.ListAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import com.ssafy.nullable.UpdateListWithNull
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListDataSourceImpl @Inject constructor(private val listAPI: ListAPI) : ListDataSource {

    override suspend fun createList(createListRequestDto: CreateListRequestDto): Flow<ListResponseDto> =
        safeApiCall { listAPI.createList(createListRequestDto) }.toFlow()

    override suspend fun updateList(listId: Long, updateListRequestDto: UpdateListRequestDto): Flow<Unit> =
        safeApiCall { listAPI.updateList(listId, updateListRequestDto) }.toFlow()

    override suspend fun updateList(
        listId: Long,
        updateListWithNull: UpdateListWithNull
    ): Flow<Unit>  = safeApiCall { listAPI.updateList(listId, updateListWithNull) }.toFlow()

    // 리스트는 삭제가 없습니다.
    override suspend fun deleteList(listId: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun setListArchive(listId: Long): Flow<Unit> =
        safeApiCall { listAPI.setListArchive(listId) }.toFlow()

    override suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>> =
        safeApiCall { listAPI.getArchivedLists(boardId) }.toFlow()

    // TODO 이런 기능은 없습니다 리스트 워치 할당 해제만 있습니다
    override suspend fun deleteListMember(memberId: Long, listId: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleListWatchBoard(memberId: Long, listId: Long): Flow<AlertResponse> =
        safeApiCall { listAPI.setAlertList(listId, memberId) }.toFlow()

}
