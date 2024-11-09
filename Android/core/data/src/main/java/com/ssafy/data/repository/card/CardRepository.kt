package com.ssafy.data.repository.card

import com.ssafy.database.dto.AttachmentEntity
import com.ssafy.database.dto.with.CardWithListAndBoardName
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.card.CardLabelUpdateDto
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.model.member.SimpleCardMemberDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardLabelWithLabelDTO
import com.ssafy.model.with.CardMemberAlarmDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.DataStatus
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun createCard(myMemberId: Long, cardRequestDto: CardRequestDto, isConnected: Boolean): Flow<Long>

    suspend fun deleteCard(cardId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateCard(
        cardUpdateRequestDto: CardUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun setCardArchive(cardId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>>

    suspend fun getLocalCreateCard(): List<CardAllInfoDTO>

    suspend fun getLocalOperationCard(): List<CardResponseDto>

    suspend fun getCard(id: Long): Flow<CardResponseDto?>

    suspend fun getCardWithListAndBoardName(cardId: Long): Flow<CardWithListAndBoardName?>

    suspend fun getAllCardsInList(listId: Long): Flow<List<CardResponseDto>>

    suspend fun getAllCardsInLists(listIds: List<Long>): Flow<List<CardResponseDto>>

    suspend fun getLocalOperationCardMember(): List<CardMemberDTO>

    suspend fun getLocalOperationCardMemberAlarm(): List<CardMemberAlarmDTO>

    suspend fun getCardRepresentativesInCard(cardId: Long): Flow<List<MemberResponseDTO>>

    suspend fun getCardRepresentativesInCards(cardIds: List<Long>): Flow<List<MemberResponseDTO>>

    suspend fun getCardMembers(cardId: Long): Flow<List<MemberResponseDTO>>

    suspend fun getMembersWithRepresentativeFlag(
        workspaceId: Long,
        boardId: Long,
        cardId: Long
    ): Flow<List<MemberResponseDTO>>

    suspend fun createCardMember(cardId: Long, memberId: Long, isStatus: DataStatus): Flow<Long>

    suspend fun updateCardMember(
        cardId: Long,
        simpleCardMemberDto: SimpleCardMemberDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun createCardWatch(cardId: Long, isStatus: DataStatus): Flow<Long>

    suspend fun getLocalCreateCardLabels(): List<CardLabelDTO>

    suspend fun getLocalOperationCardLabels(): List<CardLabelDTO>

    suspend fun getLabelFlow(id: Long): Flow<CardLabelDTO?>

    suspend fun getAllCardLabelsInCard(cardId: Long): Flow<List<CardLabelWithLabelDTO>>

    suspend fun getAllCardLabelsInCards(cardIds: List<Long>): Flow<List<CardLabelWithLabelDTO>>

    suspend fun createCardLabel(cardLabel: CardLabelDTO, isConnected: Boolean): Flow<Long>

    suspend fun updateCardLabel(id: Long, cardLabelUpdateDto: CardLabelUpdateDto, isConnected: Boolean): Flow<Unit>

    suspend fun deleteCardLabel(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getLocalCreateAttachments(): List<AttachmentDTO>

    suspend fun getLocalOperationAttachment(): List<AttachmentDTO>

    suspend fun getAttachmentFlow(id: Long): Flow<AttachmentDTO?>

    suspend fun getCoverAttachment(cardId: Long): Flow<AttachmentDTO?>

    suspend fun getAllAttachments(cardId: Long): Flow<List<AttachmentDTO>>

    suspend fun createAttachment(attachment: AttachmentDTO, isConnected: Boolean): Flow<Long>

    suspend fun deleteAttachment(id: Long, isConnected: Boolean): Flow<Unit>
}
