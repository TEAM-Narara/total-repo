package com.ssafy.board.board

import androidx.lifecycle.viewModelScope
import com.ssafy.board.GetBoardAndWorkspaceMemberUseCase
import com.ssafy.board.GetBoardUseCase
import com.ssafy.board.GetLabelUseCase
import com.ssafy.board.UpdateBoardUseCase
import com.ssafy.board.board.data.BoardData
import com.ssafy.board.board.data.BoardDataMapper
import com.ssafy.board.search.BoardSearchController
import com.ssafy.card.CreateCardUseCase
import com.ssafy.card.MoveCardUseCase
import com.ssafy.list.CreateListUseCase
import com.ssafy.list.GetLocalScreenListsInCardsFilterUseCase
import com.ssafy.list.MoveListUseCase
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
import kotlinx.coroutines.Dispatchers
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
    private val getLocalScreenListsInCardsFilterUseCase: GetLocalScreenListsInCardsFilterUseCase,
    private val createListUseCase: CreateListUseCase,
    private val updatedListUseCase: UpdateListUseCase,
    private val setListArchiveUseCase: SetListArchiveUseCase,
    private val createCardUseCase: CreateCardUseCase,
    private val moveListUseCase: MoveListUseCase,
    private val moveCardUseCase: MoveCardUseCase,
    getLabelUseCase: GetLabelUseCase,
    getBoardAndWorkspaceMemberUseCase: GetBoardAndWorkspaceMemberUseCase,
) : BaseViewModel() {
    private val _workspaceId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setWorkspaceId(workspaceId: Long) = _workspaceId.update { workspaceId }
    private val _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setBoardId(boardId: Long) = _boardId.update { boardId }

    init {
        viewModelScope.launch {
            combine(_workspaceId, _boardId) { workspaceId, boardId ->
                if (workspaceId == null || boardId == null) null
                else Pair(workspaceId, boardId)
            }.filterNotNull().collect {
                val (workspaceId, boardId) = it
                boardSearchController.setSearchParams(workspaceId, boardId)
            }
        }
    }

    val boardSearchController = BoardSearchController(
        viewModelScope,
        getLabelUseCase,
        getBoardAndWorkspaceMemberUseCase
    )

    val boardData: StateFlow<BoardData?> = combine(
        _boardId,
        boardSearchController.searchParameters
    ) { boardId, searchParameters ->
        if (boardId == null) null
        else Pair(boardId, searchParameters)
    }.filterNotNull().flatMapLatest {
        val (boardId, searchParameters) = it
        combine(
            getBoardUseCase(boardId),
            getLocalScreenListsInCardsFilterUseCase(boardId, searchParameters)
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
                listId = listId,
                updateListRequestDto = UpdateListRequestDto(listName),
                isConnected = isConnected
            )
        }
    }

    fun updateListOrder(listId: Long, prevListId: Long?, nextListId: Long?) =
        viewModelScope.launch(Dispatchers.IO) {
            val boardId = _boardId.value ?: return@launch
            withSocketState { isConnected ->
                moveListUseCase(
                    boardId = boardId,
                    listId = listId,
                    prevListId = prevListId,
                    nextListId = nextListId,
                    isConnected = isConnected
                )
            }
        }

    fun updateCardOrder(cardId: Long, targetListId: Long, prevCardId: Long?, nextCardId: Long?) =
        viewModelScope.launch(Dispatchers.IO) {
            withSocketState { isConnected ->
                moveCardUseCase(
                    cardId = cardId,
                    prevCardId = prevCardId,
                    nextCardId = nextCardId,
                    targetListId = targetListId,
                    isConnected = isConnected,
                )
            }
        }

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