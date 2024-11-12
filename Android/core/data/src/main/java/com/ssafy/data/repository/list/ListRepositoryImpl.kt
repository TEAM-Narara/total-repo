package com.ssafy.data.repository.list

import com.ssafy.data.di.IoDispatcher
import com.ssafy.database.dao.AttachmentDao
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.CardLabelDao
import com.ssafy.database.dao.CardMemberDao
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dao.ListMemberDao
import com.ssafy.database.dao.NegativeIdGenerator
import com.ssafy.database.dao.ReplyDao
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.ListMemberAlarmEntity
import com.ssafy.database.dto.ListMemberEntity
import com.ssafy.database.dto.bitmask.UpdateListBitmaskDTO
import com.ssafy.database.dto.bitmask.bitmaskColumn
import com.ssafy.database.dto.piece.LocalTable
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toDto
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.ListResponseDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ListInCard
import com.ssafy.model.with.ListInCardsDTO
import com.ssafy.model.with.ListMemberAlarmDTO
import com.ssafy.model.with.ListMemberDTO
import com.ssafy.network.source.list.ListDataSource
import com.ssafy.nullable.UpdateListWithNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
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
    private val negativeIdGenerator: NegativeIdGenerator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ListRepository {

    override suspend fun createList(
        myMemberId: Long,
        createListRequestDto: CreateListRequestDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            listDataSource.createList(createListRequestDto).map { it.listId }
        } else {
            val localListId = negativeIdGenerator.getNextNegativeId(LocalTable.LIST)

            flowOf(
                listDao.insertList(
                    ListEntity(
                        id = localListId,
                        name = createListRequestDto.listName,
                        boardId = createListRequestDto.boardId,
                        isStatus = DataStatus.CREATE
                    )
                ).also {
                    createListMember(
                        listId = localListId,
                        memberId = myMemberId,
                        isStatus = DataStatus.CREATE
                    )
                    createListWatch(
                        listId = localListId,
                        isStatus = DataStatus.CREATE
                    )
                }
            )
        }
    }

    override suspend fun updateList(
        listId: Long,
        updateListRequestDto: UpdateListRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        val list = listDao.getList(listId)

        if (list != null) {
            if (isConnected) {
                listDataSource.updateList(listId, updateListRequestDto)
            } else {
                // 변경 사항 확인하고 비트마스킹
                val newList = list.copy(name = updateListRequestDto.listName)
                val newBit = bitmaskColumn(list.columnUpdate, list, newList)

                val result = when (list.isStatus) {
                    DataStatus.STAY, DataStatus.UPDATE ->
                        listDao.updateList(
                            list.copy(
                                name = updateListRequestDto.listName,
                                isStatus = DataStatus.UPDATE,
                                columnUpdate = newBit
                            )
                        )

                    DataStatus.CREATE ->
                        listDao.updateList(newList)

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
                    // 변경 사항 확인하고 비트마스킹
                    val newList = list.copy(isArchived = !list.isArchived)
                    val newBit = bitmaskColumn(list.columnUpdate, list, newList)

                    val result = when (list.isStatus) {
                        DataStatus.STAY, DataStatus.UPDATE ->
                            listDao.updateList(
                                newList.copy(
                                    columnUpdate = newBit,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE ->
                            listDao.updateList(newList)

                        DataStatus.DELETE -> {}
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

    override suspend fun getLocalOperationList(): List<ListEntity> =
        withContext(ioDispatcher) { listDao.getLocalOperationList() }

    override suspend fun getListMembers(listId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            listMemberDao.getListMembers(listId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun createListMember(
        listId: Long,
        memberId: Long,
        isStatus: DataStatus
    ): Flow<Long> =
        withContext(ioDispatcher) {
            flowOf(
                listMemberDao.insertListMember(
                    ListMemberEntity(
                        listId = listId,
                        memberId = memberId,
                        isStatus = isStatus
                    )
                )
            )
        }

    // TODO 해당 기능은 사용하지 않습니다 리스트 멤버는 삭제가 없습니다
    // 리스트 워치 할당 해제만 있습니다
    override suspend fun deleteListMember(
        memberId: Long,
        listId: Long,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val member = listMemberDao.getListMember(memberId, listId)

            if (member != null) {
                if (isConnected) {
                    listDataSource.deleteListMember(memberId, listId)
                } else {
                    val result = when (member.isStatus) {
                        DataStatus.CREATE ->
                            listMemberDao.deleteLocalListMember(memberId, listId)

                        else ->
                            listMemberDao.updateListMember(member.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun getListWatchStatus(id: Long): Flow<Boolean?> =
        withContext(ioDispatcher) {
            listMemberDao.getListMemberAlarmFlow(id).map { it?.toDTO()?.isAlert }
        }

    override suspend fun createListWatch(listId: Long, isStatus: DataStatus): Flow<Long> =
        withContext(ioDispatcher) {
            flowOf(
                listMemberDao.insertListAlarm(
                    ListMemberAlarmEntity(
                        listId = listId,
                        isStatus = isStatus
                    )
                )
            )
        }

    // TODO 리스트 워치는 온라인일 때에만 가능합니다
    override suspend fun toggleListWatch(
        memberId: Long,
        listId: Long,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val isWatch = listDataSource.toggleListWatchBoard(memberId, listId).firstOrNull()
            isWatch?.let {
                listMemberDao.insertListAlarm(
                    ListMemberAlarmEntity(
                        listId = listId,
                        isAlert = it.isAlert,
                        isStatus = DataStatus.STAY
                    )
                )
            }
            flowOf()
//            val memberAlarm = listMemberDao.getListMemberAlarm(id)


//            if (memberAlarm != null) {
//                if (isConnected) {
//                    listDataSource.toggleListWatchBoard(id)
//                } else {
//                    val result = when (memberAlarm.isStatus) {
//                        DataStatus.STAY ->
//                            listMemberDao.updateListMemberAlarm(
//                                memberAlarm.copy(
//                                    isAlert = !memberAlarm.isAlert,
//                                    isStatus = DataStatus.UPDATE
//                                )
//                            )
//
//                        DataStatus.CREATE, DataStatus.UPDATE ->
//                            listMemberDao.updateListMemberAlarm(
//                                memberAlarm.copy(
//                                    isAlert = !memberAlarm.isAlert,
//                                )
//                            )
//
//                        DataStatus.DELETE -> {}
//                    }
//
//                    flowOf(result)
//                }
//            } else {
//                flowOf(Unit)
//            }
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

                        val cardThumbnails = cards.map { card ->
                            card.toDTO(
                                replyCount = replyCountMap[card.id]?.count ?: 0,
                                isWatch = cardWatchMap[card.id]?.isAlert ?: false,
                                isAttachment = attachmentMap[card.id]?.isAttachment ?: false,
                                cardMembers = cardMemberMap[card.id]?.map { it.toDTO() }
                                    ?: emptyList(),
                                cardLabels = cardLabelMap[card.id]?.map { it.toDto() }
                                    ?: emptyList()
                            )
                        }

                        cardThumbnails
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getLocalScreenListsInCardsFilter(boardId: Long,
                                                          includeNoRepresentative: Int,
                                                          memberIdsEmpty: Int,
                                                          memberIds: List<Long>,
                                                          noLimitDate: Int,
                                                          expireDate: Int,
                                                          deadlineDateType: Int,
                                                          includeNoLabel: Int,
                                                          labelIdsEmpty: Int,
                                                          cardLabelIds: List<Long>,
                                                          keyword: String): Flow<List<ListInCard>> {
        return listDao.getAllListsInBoard(boardId).flatMapLatest { lists ->
            val listIds = lists.map { it.id }

            combine(
                listMemberDao.getListsMemberAlarms(listIds),
                // 담당자 없음, 담당자 종류
                // 날짜 제한 없음, 기한 만료, {선택 안함(0), 내일 내(1), 일주일 내(2), 한달 내(3)}
                // 라벨 없음, 라벨 종류
                // 키워드
                cardDao.getAllCardsInListsFilter(
                    listIds,
                    includeNoRepresentative,
                    memberIdsEmpty,
                    memberIds,
                    noLimitDate,
                    expireDate,
                    deadlineDateType,
                    includeNoLabel,
                    labelIdsEmpty,
                    cardLabelIds,
                    keyword
                    ).flatMapLatest { cards ->
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

                        val cardThumbnails = cards.map { card ->
                            card.toDTO(
                                replyCount = replyCountMap[card.id]?.count ?: 0,
                                isWatch = cardWatchMap[card.id]?.isAlert ?: false,
                                isAttachment = attachmentMap[card.id]?.isAttachment ?: false,
                                cardMembers = cardMemberMap[card.id]?.map { it.toDTO() } ?: emptyList(),
                                cardLabels = cardLabelMap[card.id]?.map { it.toDto() } ?: emptyList()
                            )
                        }

                        cardThumbnails
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

    override suspend fun updateList(listId: Long, dto: UpdateListBitmaskDTO) {
        val updateDto = UpdateListWithNull(dto.name)
        listDataSource.updateList(listId, updateDto)
    }
}
