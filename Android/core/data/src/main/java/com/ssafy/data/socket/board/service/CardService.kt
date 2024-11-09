package com.ssafy.data.socket.board.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssafy.data.socket.board.model.card.AddCardMemberRequestDto
import com.ssafy.data.socket.board.model.card.AddCardRequestDto
import com.ssafy.data.socket.board.model.card.ArchiveCardRequestDto
import com.ssafy.data.socket.board.model.card.DeleteCardMemberRequestDto
import com.ssafy.data.socket.board.model.card.DeleteCardRequestDto
import com.ssafy.data.socket.board.model.card.EditCardRequestDto
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.CardMemberDao
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardMemberEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardService @Inject constructor(
    private val cardDao: CardDao,
    private val cardMemberDao: CardMemberDao,
    private val gson: Gson
) {
    suspend fun addCard(data: JsonObject) {
        val dto = gson.fromJson(data, AddCardRequestDto::class.java)
        cardDao.insertCard(
            CardEntity(
                id = dto.cardId,
                listId = dto.listId,
                name = dto.name,
                description = dto.description,
                startAt = dto.startAt,
                endAt = dto.endAt,
                coverType = dto.coverType,
                coverValue = dto.coverValue,
                isArchived = dto.isArchived,
            )
        )
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
                coverType = dto.coverType,
                coverValue = dto.coverValue,
                isArchived = dto.isArchived
            )
        )
    }

    suspend fun archiveCard(data: JsonObject) {
        val dto = gson.fromJson(data, ArchiveCardRequestDto::class.java)
        val before = cardDao.getCard(dto.cardId) ?: throw Exception("존재하지 않는 카드입니다.")
        cardDao.updateCard(
            before.copy(
                isArchived = dto.isArchived
            )
        )
    }

    suspend fun deleteCard(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteCardRequestDto::class.java)
        cardDao.deleteCardById(dto.cardId)
    }

    suspend fun addCardMember(data: JsonObject) {
        val dto = gson.fromJson(data, AddCardMemberRequestDto::class.java)
        cardMemberDao.insertCardMember(
            CardMemberEntity(
                id = dto.cardMemberId,
                cardId = dto.cardId,
                memberId = dto.memberId,
            )
        )
    }

    suspend fun deleteCardMember(data: JsonObject) {
        val dto = gson.fromJson(data, DeleteCardMemberRequestDto::class.java)
        cardMemberDao.deleteCardMemberById(dto.cardMemberId)
    }
}