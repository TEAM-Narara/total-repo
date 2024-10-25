package com.ssafy.board.board

import androidx.lifecycle.viewModelScope
import com.ssafy.board.board.data.BoardData
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ListData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor() : BaseViewModel() {
    private var _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setBoardId(boardId: Long) = _boardId.update { boardId }

    @OptIn(ExperimentalCoroutinesApi::class)
    val boardData: StateFlow<BoardData?> = _boardId.filterNotNull().flatMapLatest { boardId ->
        flow {
            val data = BoardData(
                id = boardId,
                title = "title",
                listCollection = (1..4).map { listData ->
                    ListData(
                        id = listData.toLong(),
                        title = listData.toString(),
                        cardCollection = (1..20).map { cardData ->
                            CardData(
                                id = (listData * 100 + cardData).toLong(),
                                title = cardData.toString()
                            )
                        },
                        isWatching = true
                    )
                }
            )
            delay(2000)
            emit(data)
        }.withUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun updateBoardTitle() {}
    fun updateListTitle() {}
    fun updateListOrder() {}
    fun updateCardOrder() {}
    fun addList() {}
    fun addCard() {}
    fun addPhoto() {}
}