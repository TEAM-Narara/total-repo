package com.ssafy.data.repository.card

import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.bitmask.UpdateCardBitmaskDTO
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
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun createCard(
        myMemberId: Long,
        cardRequestDto: CardRequestDto,
        isConnected: Boolean
    ): Flow<Long>

    suspend fun deleteCard(cardId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun moveCard(listId: Long,
                         cardMoveUpdateRequestDTO: List<CardMoveUpdateRequestDTO>,
                         isConnected: Boolean
    ) : Flow<Unit>

    suspend fun updateCard(cardId: Long, dto: UpdateCardBitmaskDTO): Flow<Unit>

    suspend fun setCardArchive(cardId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>>

    suspend fun getLocalCreateCard(): List<CardAllInfoDTO>

    suspend fun getLocalOperationCard(): List<CardEntity>

    suspend fun getCard(id: Long): Flow<CardResponseDto?>

    suspend fun getCardWithListAndBoardName(cardId: Long): Flow<CardWithListAndBoardNameDTO?>

    suspend fun getAllCardsInList(listId: Long): Flow<List<CardResponseDto>>

    suspend fun getAllCardsInLists(listIds: List<Long>): Flow<List<CardResponseDto>>

    suspend fun getLocalOperationCardMember(): List<CardMemberDTO>

    suspend fun deleteLocalOperationCardMember(cardMemberId: Long)

    suspend fun getLocalOperationCardMemberAlarm(): List<CardMemberAlarmDTO>

    suspend fun getCardRepresentativesInCard(cardId: Long): Flow<List<MemberResponseDTO>>

    suspend fun getCardRepresentativesInCards(cardIds: List<Long>): Flow<List<MemberResponseDTO>>

    suspend fun getCardAlertStatus(cardId: Long): Flow<Boolean>

    suspend fun setCardAlertStatus(cardId: Long, memberId: Long)

    suspend fun setCardPresenter(cardId: Long, memberId: Long): Flow<Boolean>

    suspend fun getCardMembers(cardId: Long): Flow<List<MemberResponseDTO>>

    suspend fun getMembersWithRepresentativeFlag(
        workspaceId: Long,
        boardId: Long,
        cardId: Long
    ): Flow<List<MemberWithRepresentativeDTO>>

    suspend fun createCardMember(cardId: Long, memberId: Long, isStatus: DataStatus): Flow<Long>

    suspend fun updateCardMember(
        simpleCardMemberDto: SimpleCardMemberDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun createCardWatch(cardId: Long, isStatus: DataStatus): Flow<Long>

    suspend fun getLocalCreateCardLabels(): List<CardLabelDTO>

    suspend fun getLocalOperationCardLabels(): List<CardLabelEntity>

    suspend fun getLabelFlow(id: Long): Flow<CardLabelDTO?>

    suspend fun getAllCardLabelsInCard(cardId: Long): Flow<List<CardLabelWithLabelDTO>>

    suspend fun getAllCardLabelsInCards(cardIds: List<Long>): Flow<List<CardLabelWithLabelDTO>>

    suspend fun getCardLabel(cardId: Long, labelId: Long): CardLabelDTO?

    suspend fun createCardLabel(
        createCardLabelRequestDto: CreateCardLabelRequestDto,
        isConnected: Boolean
    ): Flow<Long>

    suspend fun updateCardLabel(
        updateCardLabelActivateRequestDto: UpdateCardLabelActivateRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun deleteCardLabel(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getLocalCreateAttachments(): List<AttachmentDTO>

    suspend fun getLocalOperationAttachment(): List<AttachmentDTO>

    suspend fun getAttachmentFlow(id: Long): Flow<AttachmentDTO?>

    suspend fun getCoverAttachment(cardId: Long): Flow<AttachmentDTO?>

    suspend fun getAllAttachments(cardId: Long): Flow<List<AttachmentDTO>>

    suspend fun createAttachment(attachment: AttachmentDTO, isConnected: Boolean): Flow<Long>

    suspend fun deleteAttachment(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getLocalScreenMyRepresentativeCard(memberId: Long): Flow<List<BoardInMyRepresentativeCard>>

    suspend fun updateAttachmentToCover(id: Long, isConnected: Boolean): Flow<Unit>
}
