package com.ssafy.board.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.board.board.data.BoardData
import com.ssafy.board.board.data.CardData
import com.ssafy.board.board.data.ListData
import com.ssafy.ui.uistate.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor() : ViewModel() {
    private var _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)

    fun setBoardId(boardId: Long) = _boardId.update { boardId }

    val uiState: StateFlow<UiState<BoardData>> = _boardId.filterNotNull().map { boardId ->
        UiState.Success(
            BoardData(
                id = "board 1",
                title = "title",
                listCollection = (1..10).map { listData ->
                    ListData(
                        id = "list $listData",
                        title = listData.toString(),
                        cardCollection = (1..3).map { cardData ->
                            CardData(
                                id = "card $listData$cardData",
                                title = cardData.toString(),
                            )
                        },
                        isWatching = true
                    )
                }
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    fun updateBoardTitle() {}
    fun updateListTitle() {}
    fun updateListOrder() {}
    fun updateCardOrder() {}
    fun addList() {}
}