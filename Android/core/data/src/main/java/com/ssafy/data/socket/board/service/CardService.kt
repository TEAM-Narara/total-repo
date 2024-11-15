package com.ssafy.data.socket.board.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.image.ImageStorage
import com.ssafy.data.socket.board.model.card.AddCardLabelRequestDto
import com.ssafy.data.socket.board.model.card.AddCardMemberRequestDto
import com.ssafy.data.socket.board.model.card.AddCardRequestDto
import com.ssafy.data.socket.board.model.card.ArchiveCardRequestDto
import com.ssafy.data.socket.board.model.card.DeleteCardLabelRequestDto
import com.ssafy.data.socket.board.model.card.DeleteCardMemberRequestDto
import com.ssafy.data.socket.board.model.card.DeleteCardRequestDto
import com.ssafy.data.socket.board.model.card.EditCardRequestDto
import com.ssafy.data.socket.board.model.card.MoveCardRequestDto
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.CardLabelDao
import com.ssafy.database.dao.CardMemberDao
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.CardMemberEntity
import com.ssafy.model.with.DataStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardService @Inject constructor(
    private val cardDao: CardDao,
    private val cardMemberDao: CardMemberDao,
    private val cardLabelDao: CardLabelDao,
    private val imageStorage: ImageStorage,
    private val gson: Gson
) {
    suspend fun addCard(data: JsonObject) {
        val dto = gson.fromJson(data, AddCardRequestDto::class.java)
        val insertCard: suspend (String) -> Unit = { coverValue ->
            cardDao.insertCard(
                CardEntity(
                    id = dto.cardId,
                    listId = dto.listId,
                    name = dto.name,
                    description = dto.description,
                    startAt = dto.startAt,
                    endAt = dto.endAt,
                    coverType = dto.coverType,
                    coverValue = coverValue,
                    isArchived = dto.isArchived,
                )
            )
        }

        if (dto.coverType == "IMAGE") {
            imageStorage.saveAll(key = dto.coverValue) { path ->
                insertCard(path ?: "")
            }
        } else {
            insertCard(dto.coverValue)
        }
    }

    suspend fun editCard(data: JsonObject) {
        val dto = gson.fromJson(data, EditCardRequestDto::class.java)
        val before = cardDao.getCard(dto.cardId) ?: throw Exception("존재하지 않는 카드입니다.")

        cardDao.updateCard(
            before.copy(
                name = dto.name,
                description = dto.description,
                startAt = dto.startAt,
                endAt = dto.endAt,
                isArchived = dto.isArchived,
                isStatus = DataStatus.STAY,
                columnUpdate = 0,
            )
        )
    }

    suspend fun archiveCard(data: JsonObject) {
        val dto = gson.fromJson(data, ArchiveCardRequestDto::class.java)
        val before = cardDao.getCard(dto.cardId) ?: throw Exception("존재하지 않는 카드입니다.")
        cardDao.updateCard(
            before.copy(
                isArchived = dto.isArchived,
                isStatus = DataStatus.STAY,
                columnUpdate = 0,
            )
        )
    }

    suspend fun deleteCard(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteCardRequestDto::class.java)

        val before = cardDao.getCard(dto.cardId) ?: throw Exception("존재하지 않는 카드입니다.")
        if (before.coverType == "IMAGE") {
            before.coverValue?.let { imageStorage.delete(it) }
        }

        cardDao.deleteCardById(dto.cardId)
    }

    suspend fun addCardMember(data: JsonObject) {
        val dto = gson.fromJson(data, AddCardMemberRequestDto::class.java)
        cardMemberDao.insertCardMember(
            CardMemberEntity(
                id = dto.cardMemberId,
                cardId = dto.cardId,
                memberId = dto.memberId,
                isRepresentative = dto.isAlert,
            )
        )
    }

    suspend fun deleteCardMember(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteCardMemberRequestDto::class.java)
        val before = cardMemberDao.getCardMember(dto.cardId, dto.memberId)
            ?: throw Exception("존재하지 않는 사용자 입니다.")
        cardMemberDao.updateCardMember(
            before.copy(isRepresentative = false, isStatus = DataStatus.STAY)
        )
    }

    suspend fun addCardLabel(data: JsonObject) {
        val dto = gson.fromJson(data, AddCardLabelRequestDto::class.java)
        cardLabelDao.insertCardLabel(
            CardLabelEntity(
                id = dto.cardLabelId,
                labelId = dto.labelId,
                cardId = dto.cardId,
            )
        )
    }

    suspend fun deleteCardLabel(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteCardLabelRequestDto::class.java)
        val before = cardLabelDao.getCardLabelByCardIdAndLabelId(dto.cardId, dto.labelId)
            ?: throw Exception("존재하지 않는 카드 라벨 입니다.")
        cardLabelDao.updateCardLabel(
            before.copy(
                isActivated = false,
                isStatus = DataStatus.STAY,
            )
        )
    }

    suspend fun moveCard(data: JsonObject) {
        val dto = gson.fromJson(data, MoveCardRequestDto::class.java)
        dto.updatedCard.forEach {
            val before = cardDao.getCard(it.cardId) ?: return
            cardDao.updateCard(before.copy(listId = it.movedListId, myOrder = it.myOrder))
        }
    }
}