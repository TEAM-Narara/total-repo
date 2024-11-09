package com.ssafy.data.repository.card

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toEntity
import com.ssafy.database.dao.NegativeIdGenerator
import com.ssafy.database.dao.AttachmentDao
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.CardLabelDao
import com.ssafy.database.dao.CardMemberDao
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.piece.LocalTable
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toDto
import com.ssafy.database.dto.with.CardWithListAndBoardName
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.card.CardLabelUpdateDto
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardLabelWithLabelDTO
import com.ssafy.model.with.CardMemberAlarmDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.DataStatus
import com.ssafy.network.source.card.CardDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
    private val attachmentDao: AttachmentDao,
    private val negativeIdGenerator: NegativeIdGenerator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CardRepository {
    override suspend fun createCard(
        cardRequestDto: CardRequestDto,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            // TODO
            cardDataSource.createCard(cardRequestDto).map { 5 }
        } else {
            flowOf(cardDao.insertCard(
                CardEntity(
                    id = negativeIdGenerator.getNextNegativeId(LocalTable.CARD),
                    name = cardRequestDto.cardName,
                    listId = cardRequestDto.listId,
                    isStatus = DataStatus.CREATE)
            ))
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
        cardUpdateRequestDto: CardUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        val card = cardDao.getCard(cardUpdateRequestDto.cardId)

        if (card != null) {
            if (isConnected) {
                cardDataSource.updateCard(cardUpdateRequestDto)
            } else {
                val result = when (card.isStatus) {
                    DataStatus.STAY ->
                        cardDao.updateCard(
                            card.copy(
                                name = cardUpdateRequestDto.name,
                                description = cardUpdateRequestDto.description,
                                startAt = cardUpdateRequestDto.startAt,
                                endAt = cardUpdateRequestDto.endAt,
                                coverType = cardUpdateRequestDto.cover.type.name,
                                coverValue = cardUpdateRequestDto.cover.value,
                                isStatus = DataStatus.UPDATE
                            )
                        )

                    DataStatus.CREATE, DataStatus.UPDATE ->
                        cardDao.updateCard(
                            card.copy(
                                name = cardUpdateRequestDto.name,
                                description = cardUpdateRequestDto.description,
                                startAt = cardUpdateRequestDto.startAt,
                                endAt = cardUpdateRequestDto.endAt,
                                coverType = cardUpdateRequestDto.cover.type.name,
                                coverValue = cardUpdateRequestDto.cover.value
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

    override suspend fun setCardArchive(cardId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val card = cardDao.getCard(cardId)

            if (card != null) {
                if (isConnected) {
                    cardDataSource.setCardArchive(cardId)
                } else {
                    val result = when (card.isStatus) {
                        DataStatus.CREATE -> cardDao.deleteCard(card)
                        else -> cardDao.updateCard(card.copy(isArchived = !card.isArchived))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>>  =
        withContext(ioDispatcher) {
            cardDao.getAllCardsArchived(boardId)
                .map { entities -> entities.map { it.toDto() } }
        }

    override suspend fun getLocalCreateCard(): List<CardAllInfoDTO> =
        withContext(ioDispatcher) {
            cardDao.getLocalCreateCard()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationCard(): List<CardResponseDto> =
        withContext(ioDispatcher) {
            cardDao.getLocalOperationCard()
                .map { entity -> entity.toDto() }
        }

    override suspend fun getCard(id: Long): Flow<CardResponseDto?> =
        withContext(ioDispatcher) {
            cardDao.getCardFlow(id)
                .map { it?.toDto() }
        }

    override suspend fun getCardWithListAndBoardName(cardId: Long): Flow<CardWithListAndBoardName?> =
        withContext(ioDispatcher) {
            cardDao.getCardWithListAndBoardName(cardId)
        }

    override suspend fun getAllCardsInList(listId: Long): Flow<List<CardResponseDto>> =
        withContext(ioDispatcher) {
            cardDao.getAllCardsInList(listId)
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

    override suspend fun getCardMembers(cardId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            cardMemberDao.getCardMembers(cardId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun getLocalCreateCardLabels(): List<CardLabelDTO> =
        withContext(ioDispatcher) {
            cardLabelDao.getLocalCreateCardLabels()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationCardLabels(): List<CardLabelDTO> =
        withContext(ioDispatcher) {
            cardLabelDao.getLocalOperationCardLabels()
                .map { it.toDTO() }
        }

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

    override suspend fun createCardLabel(cardLabel: CardLabelDTO, isConnected: Boolean): Flow<Long> =
        withContext(ioDispatcher) {
            if (isConnected) {
                // TODO
                cardDataSource.createCardLabel(cardLabel).map { 5 }
            } else {
                flowOf(cardLabelDao.insertCardLabel(
                    cardLabel.copy(isStatus = DataStatus.CREATE).toEntity()
                ))
            }
        }

    override suspend fun updateCardLabel(id: Long, cardLabelUpdateDto: CardLabelUpdateDto, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val cardLabel = cardLabelDao.getCardLabel(id)

            if(cardLabel != null) {
                if (isConnected) {
                    cardDataSource.updateCardLabel(id, cardLabelUpdateDto)
                } else {
                    val result = when(cardLabel.isStatus) {
                        DataStatus.STAY ->
                            cardLabelDao.updateCardLabel(cardLabel.copy(
                                labelId = cardLabelUpdateDto.labelId,
                                cardId = cardLabelUpdateDto.cardId,
                                isActivated = cardLabelUpdateDto.isActivated,
                                isStatus = DataStatus.UPDATE
                            ))
                        DataStatus.CREATE, DataStatus.UPDATE  ->
                            cardLabelDao.updateCardLabel(cardLabel.copy(
                                labelId = cardLabelUpdateDto.labelId,
                                cardId = cardLabelUpdateDto.cardId,
                                isActivated = cardLabelUpdateDto.isActivated,
                            ))
                        DataStatus.DELETE -> { }
                    }

                    flowOf(result)
                }
            } else{
                flowOf(Unit)
            }
        }

    override suspend fun deleteCardLabel(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val label = cardLabelDao.getCardLabel(id)

            if(label != null) {
                if (isConnected) {
                    cardDataSource.deleteCardLabel(id)
                } else {
                    val result = when(label.isStatus) {
                        DataStatus.CREATE ->
                            cardLabelDao.deleteCardLabel(label)
                        else ->
                            cardLabelDao.updateCardLabel(label.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else{
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
                // TODO
                cardDataSource.createAttachment(attachment).map { 5 }
            } else {
                flowOf(attachmentDao.insertAttachment(
                    attachment.copy(
                        id = negativeIdGenerator.getNextNegativeId(LocalTable.ATTACHMENT),
                        isStatus = DataStatus.CREATE
                    ).toEntity()
                ))
            }
        }

    override suspend fun deleteAttachment(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val attachment = attachmentDao.getAttachment(id)

            if(attachment != null) {
                if (isConnected) {
                    cardDataSource.deleteAttachment(id)
                } else {
                    val result = when(attachment.isStatus) {
                        DataStatus.CREATE ->
                            attachmentDao.deleteAttachment(attachment)
                        else ->
                            attachmentDao.updateAttachment(attachment.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else{
                flowOf(Unit)
            }
        }
}
