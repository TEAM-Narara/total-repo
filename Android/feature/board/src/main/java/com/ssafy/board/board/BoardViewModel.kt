package com.ssafy.board.board

import androidx.lifecycle.viewModelScope
import com.ssafy.board.GetBoardUseCase
import com.ssafy.board.UpdateBoardUseCase
import com.ssafy.board.board.data.BoardData
import com.ssafy.board.board.data.BoardDataMapper
import com.ssafy.list.CreateListUseCase
import com.ssafy.list.GetListsUseCase
import com.ssafy.list.SetListArchiveUseCase
import com.ssafy.list.UpdateListUseCase
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.list.CreateListRequestDto
import com.ssafy.model.list.UpdateListRequestDto
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
    private val getListsUseCase: GetListsUseCase,
    private val createListUseCase: CreateListUseCase,
    private val updatedListUseCase: UpdateListUseCase,
    private val setListArchiveUseCase: SetListArchiveUseCase,
) : BaseViewModel() {
    private var _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setBoardId(boardId: Long) = _boardId.update { boardId }

    private var isConnected = MutableStateFlow(true)

    val boardData: StateFlow<BoardData?> = _boardId.filterNotNull().flatMapLatest { boardId ->
        combine(
            getBoardUseCase(boardId).withUiState(),
            getListsUseCase(boardId).withUiState()
        ) { board, lists ->
            BoardDataMapper.fromDto(board, lists)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun updateBoardName(boardName: String) = viewModelScope.launch {
        boardData.value?.let {
            updateBoardUseCase(
                id = _boardId.value ?: return@launch,
                updateBoardRequestDto = UpdateBoardRequestDto(
                    name = boardName,
                    background = it.background,
                    visibility = it.visibility,
                ),
                isConnected = isConnected.value
            )
        }
    }

    fun updateListName(listId: Long, listName: String) = viewModelScope.launch {
        updatedListUseCase(
            updateListRequestDto = UpdateListRequestDto(listId, listName),
            isConnected = false
        )
    }

    fun updateListOrder() {}
    fun updateCardOrder() {}
    fun addList(listName: String) = viewModelScope.launch {
        createListUseCase(
            createListRequestDto = CreateListRequestDto(
                boardId = _boardId.value ?: return@launch,
                listName = listName
            ),
            isConnected = false
        )
    }

    fun addCard() {}
    fun addPhoto() {}
}