package com.ssafy.board.updateboard

import androidx.lifecycle.viewModelScope
import com.ssafy.board.updateboard.data.BoardData
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
class UpdateBoardViewModel @Inject constructor() : BaseViewModel() {
    private var _boardId: MutableStateFlow<Long?> = MutableStateFlow(null)
    fun setBoardId(boardId: Long) = _boardId.update { boardId }

    val boardData: StateFlow<BoardData?> = _boardId.filterNotNull().flatMapLatest { boardId ->
        flow {
            emit(
                BoardData(
                    id = 0L,
                    visibility = "Workspace",
                    title = "title",
                    background = Unit,
                    workspaceTitle = "workspace"
                )
            )
        }.withUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )
}
