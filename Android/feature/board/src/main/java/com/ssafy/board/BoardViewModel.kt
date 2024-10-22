package com.ssafy.board

import androidx.lifecycle.ViewModel
import com.ssafy.board.data.BoardData
import com.ssafy.board.data.CardData
import com.ssafy.board.data.ListData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor() : ViewModel() {
    val uiState: StateFlow<BoardUiState> get() = _uiState
    private val _uiState = MutableStateFlow(
        BoardUiState(
            boardData = BoardData(
                id = "board 1",
                title = "title",
                listCollection = (1..3).map { listData ->
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
    )

    fun updateBoardTitle() {}
    fun updateListTitle() {}
    fun updateListOrder() {}
    fun updateCardOrder() {}
}