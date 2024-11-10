package com.ssafy.board.board

import androidx.lifecycle.viewModelScope
import com.ssafy.board.GetBoardUseCase
import com.ssafy.board.UpdateBoardUseCase
import com.ssafy.board.board.data.BoardData
import com.ssafy.board.board.data.BoardDataMapper
import com.ssafy.card.CreateCardUseCase
import com.ssafy.list.CreateListUseCase
import com.ssafy.list.GetListsInCardsUseCase
import com.ssafy.list.SetListArchiveUseCase
import com.ssafy.list.UpdateListUseCase
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.UpdateListRequestDto
import com.ssafy.model.with.ListInCard
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BoardViewModel @Inject constructor(
    private val getBoardUseCase: GetBoardUseCase,
    private val updateBoardUseCase: UpdateBoardUseCase,
    private val getListsUseCase: GetListsInCardsUseCase,
    private val createListUseCase: CreateListUseCase,
    private val updatedListUseCase: UpdateListUseCase,
    private val setListArchiveUseCase: SetListArchiveUseCase,
    private val createCardUseCase: CreateCardUseCase
) : BaseViewModel() {
    private var _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setBoardId(boardId: Long) = _boardId.update { boardId }

    val boardData: StateFlow<BoardData?> = _boardId.filterNotNull().flatMapLatest { boardId ->
        combine(
            getBoardUseCase(boardId),
            getListsUseCase(boardId)
        ) { board: BoardDTO?, lists: List<ListInCard> ->
            val filteredList = lists.filter { !it.isArchived }
            board?.let { BoardDataMapper.fromDto(board, filteredList) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun updateBoardName(boardName: String) = viewModelScope.launch {
        boardData.value?.let {
            withSocketState { isConnected ->
                updateBoardUseCase(
                    id = _boardId.value ?: return@withSocketState,
                    updateBoardRequestDto = UpdateBoardRequestDto(
                        name = boardName,
                        cover = it.cover,
                        visibility = it.visibility,
                    ),
                    isConnected = isConnected
                )
            }
        }
    }

    fun updateListName(listId: Long, listName: String) = viewModelScope.launch {
        withSocketState { isConnected ->
            updatedListUseCase(
                updateListRequestDto = UpdateListRequestDto(listId, listName),
                isConnected = isConnected
            )
        }
    }

    fun updateListOrder() {}
    fun updateCardOrder() {}
    fun addList(listName: String) = viewModelScope.launch {
        if (listName.isEmpty()) return@launch
        withSocketState { isConnected ->
            createListUseCase(
                createListRequestDto = CreateListRequestDto(
                    boardId = _boardId.value ?: return@withSocketState,
                    listName = listName
                ),
                isConnected = isConnected
            )
        }
    }

    fun addCard(listId: Long, cardName: String) = viewModelScope.launch {
        if (cardName.isEmpty()) return@launch
        withSocketState { isConnected ->
            createCardUseCase(
                cardRequestDto = CardRequestDto(
                    listId = listId,
                    cardName = cardName
                ),
                isConnected = isConnected,
            )
        }
    }

    fun addPhoto() {}
}