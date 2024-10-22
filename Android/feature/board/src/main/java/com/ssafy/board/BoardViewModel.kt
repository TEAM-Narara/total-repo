package com.ssafy.board

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor() : ViewModel() {
    val uiState: StateFlow<BoardUiState> get() = _uiState
    private val _uiState = MutableStateFlow(
        BoardUiState(
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
                    }
                )
            }
        )
    )

    fun updateBoardTitle(title: String) {}
    fun updateListTitle(title: String) {}
    fun updateCard(listId: String, cardData: CardData) {}
}