package com.ssafy.data.repository.order

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.list.ListRepository
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.ListDao
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardMyOrderRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
    private val listDao: ListDao,
    private val cardRepository: CardRepository,
    private val listRepository: ListRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): CardMyOrderRepository {
}