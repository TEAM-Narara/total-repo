package com.ssafy.data.repository.list

import com.ssafy.data.di.IoDispatcher
import com.ssafy.database.dao.AttachmentDao
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.CardLabelDao
import com.ssafy.database.dao.CardMemberDao

import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dao.ListMemberDao
import com.ssafy.database.dao.ReplyDao
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toDto
import com.ssafy.database.dto.temp.ListInCardThumbnails
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.model.with.ListInCardsDTO

import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ListInCard
import com.ssafy.model.with.ListMemberAlarmDTO
import com.ssafy.model.with.ListMemberDTO
import com.ssafy.network.source.list.ListDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepositoryImpl @Inject constructor(
    private val listDataSource: ListDataSource,
    private val listDao: ListDao,
    private val listMemberDao: ListMemberDao,
    private val cardDao: CardDao,
    private val replyDao: ReplyDao,
    private val attachmentDao: AttachmentDao,
    private val cardMemberDao: CardMemberDao,
    private val cardLabelDao: CardLabelDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ListRepository {

    override suspend fun createList(
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            listDataSource.createList(createListRequestDto).map { -1 }
        } else {
            flow {
                listDao.insertList(
                    ListEntity(
                        name = createListRequestDto.listName,
                        boardId = createListRequestDto.boardId,
                        isStatus = DataStatus.CREATE
                    )
                )
            }

        }
    }

    override suspend fun updateList(
        updateListRequestDto: UpdateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        val list = listDao.getList(updateListRequestDto.listId)

        if (list != null) {
            if (isConnected) {
                listDataSource.updateList(updateListRequestDto)
            } else {
                val result = when (list.isStatus) {
                    DataStatus.STAY ->
                        listDao.updateList(
                            list.copy(
                                name = updateListRequestDto.listName,
                                isStatus = DataStatus.UPDATE
                            )
                        )

                    DataStatus.CREATE, DataStatus.UPDATE ->
                        listDao.updateList(
                            list.copy(
                                name = updateListRequestDto.listName
                            )
                        )

                    DataStatus.DELETE -> {}
                }
                flowOf(result)
            }
        } else {
            flowOf(Unit)
        }
    }

    override suspend fun deleteList(listId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val list = listDao.getList(listId)

            if (list != null) {
                if (isConnected) {
                    listDataSource.deleteList(listId)
                } else {
                    val result = when (list.isStatus) {
                        DataStatus.CREATE -> listDao.deleteList(list)
                        else -> listDao.updateList(list.copy(isStatus = DataStatus.DELETE))
                    }
                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun setListArchive(listId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val list = listDao.getList(listId)

            if (list != null) {
                if (isConnected) {
                    listDataSource.setListArchive(listId)
                } else {
                    val result = when (list.isStatus) {
                        DataStatus.CREATE ->
                            listDao.deleteList(list)

                        else ->
                            listDao.updateList(list.copy(isArchived = !list.isArchived))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
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

            if (member != null) {
                if (isConnected) {
                    listDataSource.deleteListMember(id)
                } else {
                    when (member.isStatus) {
                        DataStatus.CREATE ->
                            listMemberDao.deleteLocalListMember(member)

                        else ->
                            listMemberDao.updateListMember(member.copy(isStatus = DataStatus.DELETE))
                    }
                }
            }
        }
    }

    override suspend fun getListWatchStatus(id: Long): Flow<Boolean> =
        withContext(ioDispatcher) {
            listMemberDao.getListMemberAlarmFlow(id).map { it.toDTO().isAlert }
        }

    override suspend fun toggleListWatch(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val memberAlarm = listMemberDao.getListMemberAlarm(id)

            if (memberAlarm != null) {
                if (isConnected) {
                    listDataSource.toggleListWatchBoard(id)
                } else {
                    val result = when (memberAlarm.isStatus) {
                        DataStatus.STAY ->
                            listMemberDao.updateListMemberAlarm(
                                memberAlarm.copy(
                                    isAlert = !memberAlarm.isAlert,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE, DataStatus.UPDATE ->
                            listMemberDao.updateListMemberAlarm(
                                memberAlarm.copy(
                                    isAlert = !memberAlarm.isAlert,
                                )
                            )

                        DataStatus.DELETE -> {}
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getLocalScreenListsInCards(boardId: Long): Flow<List<ListInCard>> {
        return listDao.getAllListsInBoard(boardId).flatMapLatest { lists ->
            val listIds = lists.map { it.id }

            combine(
                listMemberDao.getListsMemberAlarms(listIds),
                cardDao.getAllCardsInLists(listIds).flatMapLatest { cards ->
                    val cardIds = cards.map { it.id }

                    combine(
                        replyDao.getReplyCounts(cardIds),
                        cardMemberDao.getCardRepresentativesInCards(cardIds),
                        cardMemberDao.getCardsMemberAlarms(cardIds),
                        cardLabelDao.getAllCardLabelsInCards(cardIds),
                        attachmentDao.getCardsIsAttachment(cardIds)
                    ) { replyCounts, cardMembers, cardWatch, cardLabels, isAttachment ->
                        val replyCountMap = replyCounts.associateBy { it.cardId }
                        val cardMemberMap = cardMembers.groupBy { it.cardMember.cardId }
                        val cardWatchMap = cardWatch.associateBy { it.cardId }
                        val cardLabelMap = cardLabels.groupBy { it.cardLabel.cardId }
                        val attachmentMap = isAttachment.associateBy { it.cardId }

                        val updatedCards = cards.map { card ->
                            card.toDTO(
                                replyCount = replyCountMap[card.id]?.count ?: 0,
                                isWatch = cardWatchMap[card.id]?.isAlert ?: false,
                                isAttachment = attachmentMap[card.id]?.isAttachment ?: false,
                                cardMembers = cardMemberMap[card.id]?.map { it.toDTO() } ?: emptyList(),
                                cardLabels = cardLabelMap[card.id]?.map { it.toDto() } ?: emptyList()
                            )
                        }

                        updatedCards
                    }
                }
            ) { listWatch, updatedCards ->
                val listWatchMap = listWatch.associateBy { it.listId }

                lists.map { list ->
                    list.toDto(
                        cards = updatedCards.filter { it.listId == list.id },
                        isWatch = listWatchMap[list.id]?.isAlert ?: false
                    )
                }
            }
        }
    }




}
