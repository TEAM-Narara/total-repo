package com.ssafy.data.repository.list

import com.ssafy.data.di.IoDispatcher

import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.network.source.list.ListDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepositoryImpl @Inject constructor(
    private val listDataSource: ListDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ListRepository {

    override suspend fun createList(
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            listDataSource.createList(createListRequestDto)
        } else {
            TODO("Room DB가 연동되면 생성 로직을 구현해주세요")
        }
    }

    override suspend fun updateList(
        updateListRequestDto: UpdateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            listDataSource.updateList(updateListRequestDto)
        } else {
            TODO("Room DB가 연동되면 수정 로직을 구현해주세요")
        }
    }

    override suspend fun setListArchive(listId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                listDataSource.setListArchive(listId)
            } else {
                TODO("Room DB가 연동되면 아카이브 로직을 구현해주세요")
            }
        }

    override suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>> =
        withContext(ioDispatcher) {
            TODO("Room DB가 연동되면 아카이브된 리스트 조회 로직을 구현해주세요")
        }

    override suspend fun getListDetails(boardId: Long): Flow<List<ListInCardsDTO>> = withContext(ioDispatcher) {
        TODO("Room DB가 연동되면 리스트 상세 조회 로직을 구현해주세요")
    }
}
