package com.ssafy.data.repository.list

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.model.list.ListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.network.source.list.ListDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepositoryImpl @Inject constructor(
    private val listDataSource: ListDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ListRepository {

    override suspend fun createList(
        listRequestDto: ListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            listDataSource.createList(listRequestDto)
                .toFlow()
                .map { listResponseDto: ListResponseDto ->
                    TODO("Room DB가 연동되면 생성 로직을 구현해주세요")
                }
        } else {
            TODO("Room DB가 연동되면 생성 로직을 구현해주세요")
        }
    }

    override suspend fun updateList(
        listId: Long,
        listRequestDto: ListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            listDataSource.updateList(listId, listRequestDto)
                .toFlow()
                .map { listResponseDto: ListResponseDto ->
                    TODO("Room DB가 연동되면 수정 로직을 구현해주세요")
                }
        } else {
            TODO("Room DB가 연동되면 수정 로직을 구현해주세요")
        }
    }

    override suspend fun setListArchive(listId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                listDataSource.setListArchive(listId)
                    .toFlow()
                    .map { listResponseDto: ListResponseDto ->
                        TODO("Room DB가 연동되면 아카이브 로직을 구현해주세요")
                    }
            } else {
                TODO("Room DB가 연동되면 아카이브 로직을 구현해주세요")
            }
        }

    override suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>> =
        withContext(ioDispatcher) {
            TODO("Room DB가 연동되면 아카이브된 리스트 조회 로직을 구현해주세요")
        }

}
