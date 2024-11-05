package com.ssafy.data.repository.card

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.network.source.card.CardDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val cardDataSource: CardDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CardRepository {

    override suspend fun createCard(
        cardRequestDto: CardRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            cardDataSource.createCard(cardRequestDto).toFlow()
        } else {
            TODO("Room DB가 연결되면 생성로직을 구현해주세요.")
        }
    }

    override suspend fun deleteCard(cardId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                cardDataSource.deleteCard(cardId).toFlow()
            } else {
                TODO("Room DB가 연결되면 삭제로직을 구현해주세요.")
            }
        }

    override suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        if (isConnected) {
            cardDataSource.updateCard(cardId, cardUpdateRequestDto).toFlow()
        } else {
            TODO("Room DB가 연결되면 수정로직을 구현해주세요.")
        }
    }

    override suspend fun setCardArchive(cardId: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                cardDataSource.setCardArchive(cardId).toFlow()
            } else {
                TODO("Room DB가 연결되면 아카이브로직을 구현해주세요.")
            }
        }

    override suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>> =
        withContext(ioDispatcher) {
            TODO("Room DB가 연결되면 아카이브된 카드를 가져오는 로직을 구현해주세요.")
        }

}