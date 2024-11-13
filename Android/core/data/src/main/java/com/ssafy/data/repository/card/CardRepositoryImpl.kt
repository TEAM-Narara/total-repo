package com.ssafy.data.repository.card

import com.ssafy.data.di.IoDispatcher
import com.ssafy.database.dao.AttachmentDao
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.CardLabelDao
import com.ssafy.database.dao.CardMemberDao
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dao.NegativeIdGenerator
import com.ssafy.database.dao.ReplyDao
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.CardMemberAlarmEntity
import com.ssafy.database.dto.CardMemberEntity
import com.ssafy.database.dto.bitmask.UpdateCardBitmaskDTO
import com.ssafy.database.dto.bitmask.bitmaskColumn
import com.ssafy.database.dto.piece.LocalTable
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toDto
import com.ssafy.database.dto.piece.toEntity
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.card.CardLabelUpdateDto
import com.ssafy.model.card.CardMoveUpdateRequestDTO
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.label.CreateCardLabelRequestDto
import com.ssafy.model.label.UpdateCardLabelActivateRequestDto
import com.ssafy.model.list.ListMoveUpdateRequestDTO
import com.ssafy.model.member.SimpleCardMemberDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.BoardInMyRepresentativeCard
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardLabelWithLabelDTO
import com.ssafy.model.with.CardMemberAlarmDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.CardWithListAndBoardNameDTO
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.MemberWithRepresentativeDTO
import com.ssafy.network.source.card.CardDataSource
import com.ssafy.nullable.CoverWithNull
import com.ssafy.nullable.UpdateCardWithNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val cardDataSource: CardDataSource,
    private val cardDao: CardDao,
    private val cardMemberDao: CardMemberDao,
    private val cardLabelDao: CardLabelDao,
    private val replyDao: ReplyDao,
    private val attachmentDao: AttachmentDao,
    private val listDao: ListDao,
    private val boardDao: BoardDao,
    private val negativeIdGenerator: NegativeIdGenerator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CardRepository {
    override suspend fun createCard(
        myMemberId: Long,
        cardRequestDto: CardRequestDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            cardDataSource.createCard(cardRequestDto).map { it.cardSimpleResponseDto.cardId }
        } else {
            val localCardId = negativeIdGenerator.getNextNegativeId(LocalTable.CARD)

            cardDao.insertCard(
                CardEntity(
                    id = localCardId,
                    name = cardRequestDto.cardName,
                    listId = cardRequestDto.listId,
                    isStatus = DataStatus.CREATE
                )
            )

            createCardMember(
                cardId = localCardId,
                memberId = myMemberId,
                isStatus = DataStatus.CREATE
            )
            flowOf(localCardId)
        }
    }

    override suspend fun deleteCard(cardId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val card = cardDao.getCard(cardId)

            if (card != null) {
                if (isConnected) {
                    cardDataSource.deleteCard(cardId)
                } else {
                    val result = when (card.isStatus) {
                        DataStatus.CREATE -> cardDao.deleteCard(card)
                        else -> cardDao.updateCard(card.copy(isStatus = DataStatus.DELETE))
                    }
                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        val card = cardDao.getCard(cardId)

        if (card != null) {
            if (isConnected) {
                cardDataSource.updateCard(cardId, cardUpdateRequestDto)
            } else {
                // 변경 사항 확인하고 비트마스킹
                val newCard = card.copy(
                    name = card.name,
                    description = card.description,
                    startAt = card.startAt,
                    endAt = card.endAt,
                    coverType = card.coverType,
                    coverValue = card.coverValue
                )
                val newBit = bitmaskColumn(card.columnUpdate, card, newCard)

                val result = when (card.isStatus) {
                    DataStatus.STAY, DataStatus.UPDATE ->
                        cardDao.updateCard(
                            newCard.copy(
                                columnUpdate = newBit,
                                isStatus = DataStatus.UPDATE
                            )
                        )

                    DataStatus.CREATE ->
                        cardDao.updateCard(newCard)

                    DataStatus.DELETE -> {}
                }
                flowOf(result)
            }
        } else {
            flowOf(Unit)
        }
    }

    override suspend fun moveCard(
        listId: Long,
        cardMoveUpdateRequestDTO: List<CardMoveUpdateRequestDTO>,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            cardDataSource.moveCard(listId, cardMoveUpdateRequestDTO)
        } else {
            cardMoveUpdateRequestDTO.forEach {
                val card = cardDao.getCard(it.cardId) ?: return@forEach

                val newCard = card.copy(myOrder = it.myOrder)
                val newBit = bitmaskColumn(card.columnUpdate, card, newCard)

                val result = when (card.isStatus) {
                    DataStatus.STAY, DataStatus.UPDATE ->
                        cardDao.updateCard(
                            card.copy(
                                myOrder = it.myOrder,
                                isStatus = DataStatus.UPDATE,
                                columnUpdate = newBit
                            )
                        )

                    DataStatus.CREATE ->
                        cardDao.updateCard(newCard)

                    DataStatus.DELETE -> {}
                }
                flowOf(result)
            }.also {
                val list = listDao.getList(listId)

                val maxMyOrder = cardMoveUpdateRequestDTO.maxByOrNull { it.myOrder }?.myOrder ?: 0L

                if (list != null) {
                    listDao.updateList(list.copy(
                        lastCardOrder = maxMyOrder
                    ))
                }
            }
        }

        flowOf(Unit)
    }


    override suspend fun setCardArchive(cardId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val card = cardDao.getCard(cardId)

            if (card != null) {
                if (isConnected) {
                    cardDataSource.setCardArchive(cardId)
                } else {
                    // 변경 사항 확인하고 비트마스킹
                    val newCard = card.copy(isArchived = !card.isArchived)
                    val newBit = bitmaskColumn(card.columnUpdate, card, newCard)

                    val result = when (card.isStatus) {
                        DataStatus.STAY, DataStatus.UPDATE ->
                            cardDao.updateCard(
                                newCard.copy(
                                    columnUpdate = newBit,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE ->
                            cardDao.updateCard(newCard)

                        DataStatus.DELETE -> {}
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>> =
        withContext(ioDispatcher) {
            cardDao.getAllCardsArchived(boardId)
                .map { entities -> entities.map { it.toDto() } }
        }

    override suspend fun getLocalCreateCard(): List<CardAllInfoDTO> =
        withContext(ioDispatcher) {
            cardDao.getLocalCreateCard()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationCard(): List<CardEntity> =
        withContext(ioDispatcher) { cardDao.getLocalOperationCard() }

    override suspend fun getCard(id: Long): Flow<CardResponseDto?> =
        withContext(ioDispatcher) {
            cardDao.getCardFlow(id)
                .map { it?.toDto() }
        }

    override suspend fun getCardWithListAndBoardName(cardId: Long): Flow<CardWithListAndBoardNameDTO?> =
        withContext(ioDispatcher) {
            cardDao.getCardWithListAndBoardName(cardId).map { it?.toDTO() }
        }

    override suspend fun getAllCardsInList(listId: Long): Flow<List<CardResponseDto>> =
        withContext(ioDispatcher) {
            cardDao.getAllCardsInListFlow(listId)
                .map { list -> list.map { it.toDto() } }
        }

    override suspend fun getAllCardsInLists(listIds: List<Long>): Flow<List<CardResponseDto>> =
        withContext(ioDispatcher) {
            cardDao.getAllCardsInLists(listIds)
                .map { list -> list.map { it.toDto() } }
        }

    override suspend fun getLocalOperationCardMember(): List<CardMemberDTO> =
        withContext(ioDispatcher) {
            cardMemberDao.getLocalOperationCardMember()
                .map { it.toDTO() }
        }

    override suspend fun deleteLocalOperationCardMember(cardMemberId: Long) =
        withContext(ioDispatcher) {
            cardMemberDao.deleteCardMemberById(cardMemberId)
        }

    override suspend fun getLocalOperationCardMemberAlarm(): List<CardMemberAlarmDTO> =
        withContext(ioDispatcher) {
            cardMemberDao.getLocalOperationCardMemberAlarm()
                .map { it.toDTO() }
        }

    override suspend fun getCardRepresentativesInCard(cardId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            cardMemberDao.getCardRepresentativesInCard(cardId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun getCardRepresentativesInCards(cardIds: List<Long>): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            cardMemberDao.getCardRepresentativesInCards(cardIds)
                .map { list -> list.map { it.toDTO() } }
        }

    // 해당 멤버의 카드 알람 상태 조회 (멤버 id는 내것만 가능합니다.)
    override suspend fun getCardAlertStatus(cardId: Long, memberId: Long): Flow<Boolean> =
        withContext(ioDispatcher) {
            cardDataSource.getAlertCard(cardId, memberId)
        }

    // 카드 알람 상태 변경은 온라인 일 떄에만 가능합니다. (멤버 id는 내것만 가능합니다.)
    override suspend fun setCardAlertStatus(cardId: Long, memberId: Long): Flow<Boolean> =
        withContext(ioDispatcher) {
            cardDataSource.setAlertCard(cardId, memberId)
        }


    // 대표자 할당은 온라인 일 떄에만 가능합니다.
    override suspend fun setCardPresenter(cardId: Long, memberId: Long): Flow<Boolean> =
        withContext(ioDispatcher) {
            cardDataSource.setCardPresenter(cardId, memberId)
        }


    override suspend fun getCardMembers(cardId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            cardMemberDao.getCardMembers(cardId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun getMembersWithRepresentativeFlag(
        workspaceId: Long,
        boardId: Long,
        cardId: Long
    ): Flow<List<MemberWithRepresentativeDTO>> =
        withContext(ioDispatcher) {
            cardMemberDao.getMembersWithRepresentativeFlag(workspaceId, boardId, cardId)
                .map { it.map { it.toDTO() } }
        }

    override suspend fun createCardMember(
        cardId: Long,
        memberId: Long,
        isStatus: DataStatus
    ): Flow<Long> =
        withContext(ioDispatcher) {
            flowOf(
                cardMemberDao.insertCardMember(
                    CardMemberEntity(
                        cardId = cardId,
                        memberId = memberId,
                        isStatus = isStatus
                    )
                )
            )
        }

    override suspend fun updateCardMember(
        simpleCardMemberDto: SimpleCardMemberDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        val cardMember =
            cardMemberDao.getCardMember(simpleCardMemberDto.cardId, simpleCardMemberDto.memberId)

        if (cardMember != null) {
            if (isConnected) {
                cardDataSource.updateCardMember(simpleCardMemberDto.cardId, simpleCardMemberDto)
                    .map { Unit }
            } else {
                val result = when (cardMember.isStatus) {
                    DataStatus.STAY ->
                        cardMemberDao.updateCardMember(
                            cardMember.copy(
                                cardId = simpleCardMemberDto.cardId,
                                isRepresentative = !cardMember.isRepresentative,
                                isStatus = DataStatus.UPDATE
                            )
                        )

                    DataStatus.CREATE, DataStatus.UPDATE ->
                        cardMemberDao.updateCardMember(
                            cardMember.copy(
                                cardId = simpleCardMemberDto.cardId,
                                isRepresentative = !cardMember.isRepresentative,
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

    override suspend fun createCardWatch(cardId: Long, isStatus: DataStatus): Flow<Long> =
        withContext(ioDispatcher) {
            flowOf(
                cardMemberDao.insertCardAlarm(
                    CardMemberAlarmEntity(
                        cardId = cardId,
                        isStatus = isStatus
                    )
                )
            )
        }

    override suspend fun getLocalCreateCardLabels(): List<CardLabelDTO> =
        withContext(ioDispatcher) {
            cardLabelDao.getLocalCreateCardLabels()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationCardLabels(): List<CardLabelEntity> =
        withContext(ioDispatcher) { cardLabelDao.getLocalOperationCardLabels() }

    override suspend fun getLabelFlow(id: Long): Flow<CardLabelDTO?> =
        withContext(ioDispatcher) {
            cardLabelDao.getLabelFlow(id)
                .map { it?.toDTO() }
        }

    override suspend fun getAllCardLabelsInCard(cardId: Long): Flow<List<CardLabelWithLabelDTO>> =
        withContext(ioDispatcher) {
            cardLabelDao.getAllCardLabelsInCard(cardId)
                .map { list -> list.map { it.toDto() } }
        }

    override suspend fun getAllCardLabelsInCards(cardIds: List<Long>): Flow<List<CardLabelWithLabelDTO>> =
        withContext(ioDispatcher) {
            cardLabelDao.getAllCardLabelsInCards(cardIds)
                .map { list -> list.map { it.toDto() } }
        }

    override suspend fun getCardLabel(cardId: Long, labelId: Long): CardLabelDTO? =
        withContext(ioDispatcher) {
            cardLabelDao.getCardLabelByCardIdAndLabelId(cardId, labelId)?.toDTO()
        }

    override suspend fun createCardLabel(
        createCardLabelRequestDto: CreateCardLabelRequestDto,
        isConnected: Boolean
    ): Flow<Long> =
        withContext(ioDispatcher) {
            if (isConnected) {
                cardDataSource.createCardLabel(createCardLabelRequestDto).map { it.cardLabelId }
            } else {
                flowOf(
                    cardLabelDao.insertCardLabel(
                        CardLabelEntity(
                            labelId = createCardLabelRequestDto.labelId,
                            cardId = createCardLabelRequestDto.cardId,
                        )
                    )
                )
            }
        }

    override suspend fun updateCardLabel(
        updateCardLabelActivateRequestDto: UpdateCardLabelActivateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val cardLabel = cardLabelDao.getCardLabelByCardIdAndLabelId(
                updateCardLabelActivateRequestDto.cardId,
                updateCardLabelActivateRequestDto.labelId
            )

            if (cardLabel != null) {
                if (isConnected) {
                    cardDataSource.updateCardLabel(updateCardLabelActivateRequestDto).map { Unit }
                } else {
                    val result = when (cardLabel.isStatus) {
                        DataStatus.STAY ->
                            cardLabelDao.updateCardLabel(
                                cardLabel.copy(
                                    isActivated = !cardLabel.isActivated,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE, DataStatus.UPDATE ->
                            cardLabelDao.updateCardLabel(
                                cardLabel.copy(
                                    isActivated = !cardLabel.isActivated,
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

    override suspend fun deleteCardLabel(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val label = cardLabelDao.getCardLabel(id)

            if (label != null) {
                if (isConnected) {
                    cardDataSource.deleteCardLabel(id)
                } else {
                    val result = when (label.isStatus) {
                        DataStatus.CREATE ->
                            cardLabelDao.deleteCardLabel(label)

                        else ->
                            cardLabelDao.updateCardLabel(label.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun getLocalCreateAttachments(): List<AttachmentDTO> =
        withContext(ioDispatcher) {
            attachmentDao.getLocalCreateAttachments()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationAttachment(): List<AttachmentDTO> =
        withContext(ioDispatcher) {
            attachmentDao.getLocalOperationAttachments()
                .map { it.toDTO() }
        }

    override suspend fun getAttachmentFlow(id: Long): Flow<AttachmentDTO?> =
        withContext(ioDispatcher) {
            attachmentDao.getAttachmentFlow(id)
                .map { it?.toDTO() }
        }

    override suspend fun getCoverAttachment(cardId: Long): Flow<AttachmentDTO?> =
        withContext(ioDispatcher) {
            attachmentDao.getCoverAttachment(cardId)
                .map { it?.toDTO() }
        }

    override suspend fun getAllAttachments(cardId: Long): Flow<List<AttachmentDTO>> =
        withContext(ioDispatcher) {
            attachmentDao.getAllAttachments(cardId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun createAttachment(
        attachment: AttachmentDTO,
        isConnected: Boolean
    ): Flow<Long> =
        withContext(ioDispatcher) {
            if (isConnected) {
                cardDataSource.createAttachment(attachment).map { it.attachmentId }
            } else {
                flowOf(
                    attachmentDao.insertAttachment(
                        attachment.copy(
                            id = negativeIdGenerator.getNextNegativeId(LocalTable.ATTACHMENT),
                            isStatus = DataStatus.CREATE
                        ).toEntity()
                    )
                )
            }
        }

    override suspend fun deleteAttachment(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val attachment = attachmentDao.getAttachment(id)

            if (attachment != null) {
                if (isConnected) {
                    cardDataSource.deleteAttachment(id)
                } else {
                    val result = when (attachment.isStatus) {
                        DataStatus.CREATE ->
                            attachmentDao.deleteAttachment(attachment)

                        else ->
                            attachmentDao.updateAttachment(attachment.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun updateAttachmentToCover(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val attachment = attachmentDao.getAttachment(id)

            if (attachment != null) {
                if (isConnected) {
                    cardDataSource.updateAttachmentToCover(id)
                } else {
                    val result = when (attachment.isStatus) {
                        DataStatus.STAY ->
                            attachmentDao.updateAttachment(
                                attachment.copy(
                                    isCover = !attachment.isCover,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE, DataStatus.UPDATE ->
                            attachmentDao.updateAttachment(
                                attachment.copy(
                                    isCover = !attachment.isCover,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getLocalScreenMyRepresentativeCard(memberId: Long): Flow<List<BoardInMyRepresentativeCard>> {
        return cardMemberDao.getRepresentativeCardMember(memberId)
            .flatMapLatest { representativeCardIds ->
                if (representativeCardIds.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    cardDao.getCardsToList(representativeCardIds).flatMapLatest { cards ->
                        val listIds = cards.map { it.listId }.distinct()

                        listDao.getAllListsToBoard(listIds).flatMapLatest { lists ->
                            val listIdToBoardId = lists.associate { it.id to it.boardId }
                            val boardIds = lists.map { it.boardId }.distinct()

                            boardDao.getAllBoards(boardIds).flatMapLatest { boards ->
                                val boardIdToBoard = boards.associateBy { it.id }

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
                                            isAttachment = attachmentMap[card.id]?.isAttachment
                                                ?: false,
                                            cardMembers = cardMemberMap[card.id]?.map { it.toDTO() }
                                                ?: emptyList(),
                                            cardLabels = cardLabelMap[card.id]?.map { it.toDto() }
                                                ?: emptyList()
                                        )
                                    }

                                    val boardIdToCards = cardThumbnails.groupBy { thumbnail ->
                                        val listId = thumbnail.listId
                                        listIdToBoardId[listId] ?: -1L
                                    }

                                    boardIdToCards.mapNotNull { (boardId, thumbnails) ->
                                        val board = boardIdToBoard[boardId]
                                        if (board != null) {
                                            BoardInMyRepresentativeCard(
                                                id = board.id,
                                                workspaceId = board.workspaceId,
                                                name = board.name,
                                                coverType = board.coverType,
                                                coverValue = board.coverValue,
                                                visibility = board.visibility,
                                                isClosed = board.isClosed,
                                                isStatus = board.isStatus,
                                                cards = thumbnails
                                            )
                                        } else {
                                            null
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    override suspend fun updateCard(cardId: Long, dto: UpdateCardBitmaskDTO): Flow<Unit> {
        val updateDto = UpdateCardWithNull(
            name = dto.name,
            description = dto.description,
            startAt = dto.startAt,
            endAt = dto.endAt,
            cover = dto.cover?.let { CoverWithNull(it.type, it.value) }
        )
        return cardDataSource.updateCard(cardId, updateDto)
    }

}
