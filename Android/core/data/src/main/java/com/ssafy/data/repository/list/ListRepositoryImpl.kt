package com.ssafy.data.repository.list

import com.ssafy.data.di.IoDispatcher
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dao.ListMemberDao
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toDto
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.list.ListResponseDto

import com.ssafy.model.list.ListRequestDto
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ListMemberAlarmDTO
import com.ssafy.model.with.ListMemberDTO
import com.ssafy.network.source.list.ListDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepositoryImpl @Inject constructor(
    private val listDataSource: ListDataSource,
    private val listDao: ListDao,
    private val listMemberDao: ListMemberDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ListRepository {

    override suspend fun createList(
        listRequestDto: ListRequestDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            listDataSource.createList(listRequestDto)
        } else {
            flow { listDao.insertList(ListEntity(
                name = listRequestDto.listName,
                boardId = listRequestDto.boardId
            )) }

        }
    }

    override suspend fun updateList(
        listId: Long,
        listRequestDto: ListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val list = listDao.getList(listId)

            if(list != null) {
                if (isConnected) {
                    listDataSource.updateList(listId, listRequestDto)
                } else {
                    when(list.isStatus) {
                        DataStatus.STAY ->
                            listDao.updateList(list.copy(
                                boardId = listRequestDto.boardId,
                                name = listRequestDto.listName,
                                isStatus = DataStatus.UPDATE))
                        DataStatus.CREATE, DataStatus.UPDATE  ->
                            listDao.updateList(list.copy(
                                boardId = listRequestDto.boardId,
                                name = listRequestDto.listName))
                        DataStatus.DELETE -> { }
                    }
                }
            }
        }
    }

    override suspend fun deleteList(listId: Long, isConnected: Boolean): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val list = listDao.getList(listId)

            if(list != null) {
                if (isConnected) {
                    listDataSource.deleteList(listId)
                } else {
                    when(list.isStatus) {
                        DataStatus.CREATE ->
                            listDao.deleteList(list)
                        else ->
                            listDao.updateList(list.copy(isStatus = DataStatus.DELETE))
                    }
                }
            }
        }
    }

    override suspend fun setListArchive(listId: Long, isConnected: Boolean): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val list = listDao.getList(listId)

            if(list != null) {
                if (isConnected) {
                    listDataSource.setListArchive(listId)
                } else {
                    when(list.isStatus) {
                        DataStatus.CREATE ->
                            listDao.deleteList(list)
                        else ->
                            listDao.updateList(list.copy(isArchived = !list.isArchived))
                    }
                }
            }
        }
    }

    override suspend fun getLists(boardId: Long): Flow<List<ListResponseDto>> =
        withContext(ioDispatcher) {
            listDao.getAllListsInBoard(boardId)
                .map { entities -> entities.map { it.toDto() } }
        }

    override suspend fun getArchivedLists(boardId: Long): Flow<List<ListResponseDto>> =
        withContext(ioDispatcher) {
            listDao.getAllListsArchived(boardId)
                .map { entities -> entities.map { it.toDto() } }
        }

    override suspend fun getLocalCreateList(): List<ListInCardsDTO> =
        withContext(ioDispatcher) {
            listDao.getLocalCreateList()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationList(): List<ListResponseDto> =
        withContext(ioDispatcher) {
            listDao.getLocalOperationList()
                .map { entity -> entity.toDto() }
        }

    override suspend fun getListMembers(listId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            listMemberDao.getListMembers(listId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun deleteListMember(id: Long, isConnected: Boolean): Flow<Unit> = flow {
        withContext(ioDispatcher) {
            val member = listMemberDao.getListMember(id)

            if(member != null) {
                if (isConnected) {
                    listDataSource.deleteListMember(id)
                } else {
                    when(member.isStatus) {
                        DataStatus.CREATE ->
                            listMemberDao.deleteLocalListMember(member)
                        else ->
                            listMemberDao.updateListMember(member.copy(isStatus = DataStatus.DELETE))
                    }
                }
            }
        }
    }

    override suspend fun getLocalOperationListMember(): List<ListMemberDTO> =
        withContext(ioDispatcher) {
            listMemberDao.getLocalOperationListMember()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationListMemberAlarm(): List<ListMemberAlarmDTO> =
        withContext(ioDispatcher) {
            listMemberDao.getLocalOperationListMemberAlarm()
                .map { it.toDTO() }
        }

}
